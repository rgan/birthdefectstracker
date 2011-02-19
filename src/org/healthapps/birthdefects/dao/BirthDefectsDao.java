package org.healthapps.birthdefects.dao;

import org.healthapps.birthdefects.model.*;
import org.compass.core.CompassHits;

import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.Set;

public interface BirthDefectsDao {

    Collection<BirthDefect> allBirthDefects();

    void store(BirthDefect birthDefect);

    BirthDefect findBirthDefectByName(String name);

    BirthDefect getBirthDefectById(Long id);
    
    void store(Person person);
    
    Person findPersonByName(String name);

    Person findPersonById(Long id, User user);

    Collection<Person> search(Date fromDate, Date toDate, Long defectId, User searchForUser, int maxReturned);

    Collection<Person> search(Date fromDate, Date toDate, Long defectId, int maxReturned);

    Collection<? extends Locatable> search(SpatialExtent extent, int maxReturned);

    void store(EnvHazard hazard);

    EnvHazard findEnvHazardByName(String name);

    Collection<EnvHazard> searchHazards(String text, int maxReturned);

    EnvHazard findEnvHazardById(Long id);

    void delete(Person person);

    Collection mapSummaries(String summaryTypeName);

    User findUserById(Long id);

    Collection<Person> getDefectsSummaryByCode(CSquareCode cSquareCode, int maxReturned);

    void delete(EnvHazard hazard);

    Set<Long> idsFromNames(String csv);
}
