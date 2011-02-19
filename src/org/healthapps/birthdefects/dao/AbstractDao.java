package org.healthapps.birthdefects.dao;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.List;

public class AbstractDao {
    protected PersistenceManagerFactory pmf;

    public AbstractDao(PersistenceManagerFactory factory) {
        pmf = factory;
    }

    protected void closeTransactionAndPM(PersistenceManager pm) {
        if (pm.currentTransaction().isActive()) {
            pm.currentTransaction().rollback();
        }
        pm.close();
    }

    protected Object findByName(String name, String className) {
        PersistenceManager pm = pmf.getPersistenceManager();
        String query = "select from " + className + " where name == findName PARAMETERS java.lang.String findName order by name ascending";
        try {
            List defects = (List) pm.newQuery(query).execute(name);
            if (defects.isEmpty()) {
                return null;
            }
            return pm.detachCopy(defects.get(0));
        } finally {
            pm.close();
        }
    }

    protected PersistenceManager getPM() {
        return pmf.getPersistenceManager();
    }

    protected Object findById(Long id, String className) {
        PersistenceManager pm = getPM();
        try {
            final List result = findByIdWithoutDetaching(pm, id, className);
            if (result.isEmpty()) {
                return null;
            }
            return pm.detachCopy(result.get(0));
        } finally {
            pm.close();
        }
    }

    protected List findByIdWithoutDetaching(PersistenceManager pm, Long id, String className) {
        String query = "select from " + className + " where id == givenId PARAMETERS long givenId order by name ascending";
        return (List) pm.newQuery(query).execute(id);
    }

    protected void storeObject(Object jdoObject) {
        PersistenceManager pm = getPM();
        try {
            pm.currentTransaction().begin();
            pm.makePersistent(jdoObject);
            pm.currentTransaction().commit();
        } finally {
            closeTransactionAndPM(pm);
        }
    }

    protected void deleteById(Long id, String className) {
        PersistenceManager pm = getPM();
        try {
            pm.currentTransaction().begin();
            deleteWithoutTransactions(id, className, pm);
            pm.currentTransaction().commit();
        } finally {
            closeTransactionAndPM(pm);
        }
    }

    protected void deleteWithoutTransactions(Long id, String className, PersistenceManager pm) {
        List results = findByIdWithoutDetaching(pm, id, className);
        pm.deletePersistent(results.get(0));
    }
}
