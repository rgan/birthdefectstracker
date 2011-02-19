package org.healthapps.birthdefects.dao;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.model.UserBuilder;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class UserDaoImplTest extends AbstractTestDao {
    private UserDao dao;

    public void setUp() {
        super.setUp();
        dao = new UserDaoImpl(PMF.get());
    }

    @Test
    public void testShouldSaveUser() {
        User user = new UserBuilder().withUserName("aname")
                .withPassword("pwd").withRoles("user,admin")
                .withMedicalProfessional(true).build();
        dao.save(user);
        final PreparedQuery preparedQuery = getPreparedQuery(User.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals("aname", entity.getProperty("name"));
        assertEquals("pwd", entity.getProperty("password"));
        assertEquals("user,admin", entity.getProperty("roles"));
        assertTrue((Boolean) entity.getProperty("medicalProfessional"));
    }

    @Test
    public void testShouldRetrieveAllUsers() {
        assertTrue(dao.allUsers().size() == 0);
        User user = new UserBuilder().build();
        dao.save(user);
        assertTrue(dao.allUsers().size() == 1);
    }
}
