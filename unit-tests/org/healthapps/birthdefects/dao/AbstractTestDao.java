package org.healthapps.birthdefects.dao;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.junit.After;
import org.junit.Before;

public class AbstractTestDao {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    protected PreparedQuery getPreparedQuery(String className) {
        Query query = new Query(className);
        final PreparedQuery preparedQuery = DatastoreServiceFactory.getDatastoreService().prepare(query);
        return preparedQuery;
    }
}
