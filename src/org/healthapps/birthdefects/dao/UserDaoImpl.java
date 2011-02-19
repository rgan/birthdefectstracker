package org.healthapps.birthdefects.dao;

import org.healthapps.birthdefects.model.User;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.Collection;
import java.util.List;

public class UserDaoImpl extends AbstractDao implements UserDao, UserDetailsService {

    public UserDaoImpl(PersistenceManagerFactory pmf) {
        super(pmf);
    }

    public UserDaoImpl() {
        this(PMF.get());
    }

    public void save(User user) {
        PersistenceManager pm = getPM();
        try {
            pm.currentTransaction().begin();
            if (user.getId() != null) {
                User userFromDb = pm.getObjectById(User.class, user.getId());
                if (userFromDb == null) {
                    throw new JDOObjectNotFoundException("No user found for id");
                }
                userFromDb.updateFrom(user);
                pm.makePersistent(userFromDb);
            } else {
                pm.makePersistent(user);
            }
            pm.currentTransaction().commit();
        } finally {
            closeTransactionAndPM(pm);
        }
    }

    public Collection<User> allUsers() {
        PersistenceManager pm = getPM();
        String query = "select from " + User.class.getName() + " where " + " enabled == true order by name ascending";
        try {
            List<User> users = (List<User>) pm.newQuery(query).execute();
            return pm.detachCopyAll(users);
        } finally {
            pm.close();
        }
    }

    public User findByName(String name) {
        return (User) findByName(name, User.class.getName());
    }

    public void disableUser(Long id) {
        PersistenceManager pm = getPM();
        try {
            pm.currentTransaction().begin();
            List results = findByIdWithoutDetaching(pm, id, User.class.getName());
            User user = (User) results.get(0);
            user.setEnabled(false);
            pm.currentTransaction().commit();
        } finally {
            closeTransactionAndPM(pm);
        }
    }

    public User findUserById(Long id) {
        return (User) findById(id, User.class.getName());
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        final User user = findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
