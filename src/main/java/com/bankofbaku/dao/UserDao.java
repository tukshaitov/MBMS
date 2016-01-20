package com.bankofbaku.dao;

import com.bankofbaku.model.Role;
import com.bankofbaku.model.RoleDefinition;
import com.bankofbaku.model.User;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository(value = "userDao")
public class UserDao {
    static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @PersistenceContext(unitName = "MBMS")
    private EntityManager em;

    public List<RoleDefinition> getAllRoleDefinitions() {
        return em.createNamedQuery("RoleDefinition.findAll", RoleDefinition.class).getResultList();
    }

    public List<RoleDefinition> getRoleDefinitionsByUser(User user) {
        return em.createNamedQuery("RoleDefinition.findByUser", RoleDefinition.class).setParameter("user", user).getResultList();
    }

    public RoleDefinition getRoleDefinitionById(Integer id) {
        return em.createNamedQuery("RoleDefinition.findById", RoleDefinition.class).setParameter("id", id).getSingleResult();
    }

    public User getUserByLogin(String login) {
        return em.createNamedQuery("User.findByLogin", User.class).setParameter("login", login).getSingleResult();
    }

    public List<User> getAllUsers() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public void saveOrUpdateUser(User user) {
        Session session = em.unwrap(Session.class);
        session.saveOrUpdate(user);
    }

    public void saveOrUpdateRole(Role role) {
        Session session = em.unwrap(Session.class);
        session.saveOrUpdate(role);
    }

    public void deleteRole(User user, RoleDefinition roleDefinition) {
        em.createNamedQuery("Role.deleteByUserAndRoleDefinition").setParameter("user", user).setParameter("roleDefinition", roleDefinition).executeUpdate();
    }
}
