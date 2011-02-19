package org.healthapps.birthdefects.dao;

import org.compass.core.Compass;
import org.compass.core.CompassHits;
import org.compass.core.CompassSearchSession;
import org.healthapps.birthdefects.model.*;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.*;

import com.google.common.collect.Sets;

public class BirthDefectsDaoImpl extends AbstractDao implements BirthDefectsDao {

    private Compass compass;

    public BirthDefectsDaoImpl(PersistenceManagerFactory factory, Compass compass) {
        super(factory);
        this.compass = compass;
    }

    public BirthDefectsDaoImpl() {
        this(PMF.get(), CompassHelper.getCompass());
    }

    public Collection<BirthDefect> allBirthDefects() {
        PersistenceManager pm = getPM();
        String query = "select from " + BirthDefect.class.getName();
        try {
            List<BirthDefect> birthDefects = (List<BirthDefect>) pm.newQuery(query).execute();
            return pm.detachCopyAll(birthDefects);
        } finally {
            pm.close();
        }
    }

    public void store(BirthDefect birthDefect) {
        storeObject(birthDefect);
    }

    public void store(Person person) {
        PersistenceManager pm = getPM();
        try {
            //pm.currentTransaction().begin();
            if (person.getId() != null) {
                Person personFromDb = pm.getObjectById(Person.class, person.getId());
                if (personFromDb == null) {
                    throw new JDOObjectNotFoundException("No person found for id");
                }
                personFromDb.updateFrom(person);
            } else {
                pm.makePersistent(person);
            }
            updateSpatialSummary(pm, person.getCSCode(), true);
            //pm.currentTransaction().commit();
        } finally {
            pm.close();
        }
    }

    private void updateSpatialSummary(PersistenceManager pm, CSquareCode csCode, boolean increment) {
        updateTensSummary(pm, csCode.getTens(), increment);
        updateRestOfSummary(pm, csCode, increment);
    }

    private void updateRestOfSummary(PersistenceManager pm, CSquareCode csCode, boolean increment) {
        String queryString = "select from " + SpatialSummary.class.getName() + " where tensCode == tensCodeValue && unitsCode == unitsCodeValue && tenthsCode == tenthsCodeValue PARAMETERS int tensCodeValue, int unitsCodeValue, int tenthsCodeValue";
        Query query = pm.newQuery(queryString);
        List<SpatialSummary> summaries = (List<SpatialSummary>) query.execute(csCode.getTens(), csCode.getUnits(), csCode.getTenths());
        if (summaries.size() > 0) {
            SpatialSummary summary = summaries.get(0);
            if (increment) {
                summary.incrementCount();
            } else {
                summary.decrementCount();
            }
        } else {
            pm.makePersistent(new SpatialSummary(csCode));
        }
    }

    public void updateTensSummary(PersistenceManager pm, int tensCode, boolean increment) {
        String queryString = "select from " + SpatialTensSummary.class.getName() + " where tensCode == tensCodeValue PARAMETERS int tensCodeValue";
        Query query = pm.newQuery(queryString);
        List<SpatialTensSummary> tensSummaries = (List<SpatialTensSummary>) query.execute(tensCode);
        if (tensSummaries.size() > 0) {
            SpatialTensSummary summary = tensSummaries.get(0);
            if (increment) {
                summary.incrementCount();
            } else {
                summary.decrementCount();
            }
        } else {
            pm.makePersistent(new SpatialTensSummary(tensCode, 1L));
        }
    }

    public Person findPersonByName(String name) {
        return (Person) findByName(name, Person.class.getName());
    }

    public Collection<Person> search(Date fromDate, Date toDate, Long defectId, int maxReturned) {
        PersistenceManager pm = getPM();
        try {
            List<Person> persons = search(fromDate, toDate, defectId, maxReturned, pm);
            return pm.detachCopyAll(persons);
        } finally {
            pm.close();
        }
    }

    private List<Person> search(Date fromDate, Date toDate, Long defectId, int maxReturned, PersistenceManager pm) {
        String defectFilter = defectId == null ? "" : " birthDefectIds == defectId && ";
        String queryString = "select from " + Person.class.getName() + " where " + defectFilter + " dateOfBirth < toDate && dateOfBirth > fromDate PARAMETERS long defectId,  java.util.Date toDate, java.util.Date fromDate order by dateOfBirth ascending";
        Query query = pm.newQuery(queryString);
        query.setRange(0, maxReturned);
        List<Person> persons = (List<Person>) query.executeWithArray(defectId, toDate, fromDate);
        return persons;
    }

    public Collection<Person> search(Date fromDate, Date toDate, Long defectId, User searchForUser, int maxReturned) {
        PersistenceManager pm = getPM();
        try {
            List<Person> persons = search(fromDate, toDate, defectId, searchForUser, maxReturned, pm);
            return pm.detachCopyAll(persons);
        } finally {
            pm.close();
        }
    }

    private List<Person> search(Date fromDate, Date toDate, Long defectId, User searchForUser, int maxReturned, PersistenceManager pm) {
        String defectFilter = defectId == null ? "" : " birthDefectIds == defectId && ";
        String queryString = "select from " + Person.class.getName() + " where " + defectFilter + " userId == uid && dateOfBirth < toDate && dateOfBirth > fromDate PARAMETERS long defectId, long uid, java.util.Date toDate, java.util.Date fromDate order by dateOfBirth ascending";
        Query query = pm.newQuery(queryString);
        query.setRange(0, maxReturned);
        List<Person> persons = (List<Person>) query.executeWithArray(defectId, searchForUser.getId(), toDate, fromDate);
        return persons;
    }

    // GAE supports only one inequality filter per query
    // GeoHash workaround does not work well:
    // http://labs.metacarta.com/blog/27.entry/geographic-queries-on-google-app-engine/
    // http://public.grupoinnovant.com/blog/?p=23
    public Collection<? extends Locatable> search(SpatialExtent extent, int maxReturned) {
        PersistenceManager pm = getPM();
        try {
            final List filtered = new ArrayList();
            filtered.addAll(findByExtent(extent, maxReturned, pm, Person.class.getName(), Person.LAT_FIELD));
            filtered.addAll(findByExtent(extent, maxReturned, pm, EnvHazard.class.getName(), EnvHazard.LAT_FIELD));
            return pm.detachCopyAll(filtered);
        } finally {
            pm.close();
        }
    }

    private List<? extends Locatable> findByExtent(SpatialExtent extent, int maxReturned, PersistenceManager pm, String className, String fieldName) {
        String queryString = "select from " + className + " where " + fieldName + " < neLat && " + fieldName + " > swLat PARAMETERS java.lang.Double neLat, java.lang.Double swLat order by " + fieldName + " ascending";
        Query query = pm.newQuery(queryString);
        query.setRange(0, maxReturned);
        List locatables = (List) query.executeWithArray(extent.getNELat(), extent.getSWLat());
        final List<? extends Locatable> filteredLocatables = filterByExtent(locatables, extent);
        return filteredLocatables;
    }

    public void store(EnvHazard hazard) {
        // no transactions - causes problem with Compass mirroring
        PersistenceManager pm = getPM();
        try {
            pm.makePersistent(hazard);
        } finally {
            pm.close();
        }
    }

    public EnvHazard findEnvHazardByName(String name) {
        return (EnvHazard) findByName(name, EnvHazard.class.getName());
    }

    public Collection<EnvHazard> searchHazards(String text, int maxReturned) {
        List<EnvHazard> results = new ArrayList<EnvHazard>();
        CompassSearchSession search = compass.openSearchSession();
        try {
            final CompassHits hits = search.find(text);
            for (int i = 0; i < hits.length(); i++) {
                results.add(((EnvHazard) hits.data(i)));
                if (i > maxReturned) {
                    break;
                }
            }
            return results;
        } finally {
            search.close();
        }
    }

    public EnvHazard findEnvHazardById(Long id) {
        return (EnvHazard) findById(id, EnvHazard.class.getName());
    }

    public void delete(Person person) {
        deleteById(person.getId(), Person.class.getName());
        final PersistenceManager pm = getPM();
        updateSpatialSummary(pm, person.getCSCode(), false);
        pm.close();
    }

    public Collection mapSummaries(String className) {
        PersistenceManager pm = getPM();
        String query = "select from " + className + " where count > 0";
        try {
            List summaries = (List) pm.newQuery(query).execute();
            return pm.detachCopyAll(summaries);
        } finally {
            pm.close();
        }
    }

    public User findUserById(Long id) {
        return (User) findById(id, User.class.getName());
    }

    public Collection<Person> getDefectsSummaryByCode(CSquareCode cSquareCode, int maxReturned) {
        PersistenceManager pm = getPM();
        String filter = " csTens == tensCode";
        String params = " PARAMETERS int tensCode";
        if (cSquareCode.getUnits() > 0) {
            filter = filter + " && csUnits == unitsCode";
            params = params + " , int unitsCode ";
        }
        String queryString = "select from " + Person.class.getName() + " where " + filter + params + " order by dateOfBirth descending";
        try {
            Query query = pm.newQuery(queryString);
            query.setRange(0, maxReturned);
            List<Person> persons;
            if (cSquareCode.getUnits() > 0) {
                persons = (List<Person>) query.execute(cSquareCode.getTens(), cSquareCode.getUnits());
            } else {
                persons = (List<Person>) query.execute(cSquareCode.getTens());
            }
            return pm.detachCopyAll(persons);
        } finally {
            pm.close();
        }
    }

    public void delete(EnvHazard hazard) {
        // no transactions - causes problem with Compass
        deleteWithoutTransactions(hazard.getId(), EnvHazard.class.getName(), getPM());
    }

    public Set<Long> idsFromNames(String csv) {
        final String[] values = csv.split(",");
        Set<Long> defectIds = new HashSet<Long>();
        for (String v : values) {
            for (BirthDefect defect : allBirthDefects()) {
                if (defect.getName().equals(v)) {
                    defectIds.add(defect.getId());
                }
            }
        }
        return defectIds;
    }

    private List<? extends Locatable> filterByExtent(List<? extends Locatable> locatableObjects, SpatialExtent extent) {
        List<Locatable> filtered = new ArrayList<Locatable>();
        for (Locatable locatableObject : locatableObjects) {
            if (extent.contains(locatableObject.geoLocation())) {
                filtered.add(locatableObject);
            }
        }
        return filtered;
    }

    public BirthDefect findBirthDefectByName(String name) {
        return (BirthDefect) findByName(name, BirthDefect.class.getName());

    }

    public BirthDefect getBirthDefectById(Long id) {
        return (BirthDefect) findById(id, BirthDefect.class.getName());
    }


    public Person findPersonById(Long id, User user) {
        Person p = (Person) findById(id, Person.class.getName());
        if (p != null && p.getCreatedById().equals(user.getId())) {
            return p;
        }
        return null;
    }


}
