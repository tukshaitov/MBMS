package com.bankofbaku.service;

import com.bankofbaku.dao.UserDao;
import com.bankofbaku.exception.PasswordCheckException;
import com.bankofbaku.model.Role;
import com.bankofbaku.model.RoleDefinition;
import com.bankofbaku.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service(value = "userService")
public class UserService implements Serializable {
    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final long serialVersionUID = 1L;

    @Autowired
    private StandardPasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Transactional(readOnly = true)
    public User getUserByLogin(String login) {
        return userDao.getUserByLogin(login);
    }

    @Transactional(readOnly = true)
    public User get(String login) {
        return userDao.getUserByLogin(login);
    }

    @Transactional
    public void update(String login, String oldPassword, String newPassword) {
        User user = userDao.getUserByLogin(login);

        if (user != null) {
            if (oldPassword != null && newPassword != null) {
                String hashPassword = user.getPassword();
                if (passwordEncoder.matches(oldPassword, hashPassword))
                    user.setPassword(passwordEncoder.encode(newPassword));
                else
                    throw new PasswordCheckException("Password doesn't match.");
            }
        }

        userDao.saveOrUpdateUser(user);

    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Transactional(readOnly = true)
    public List<RoleDefinition> getAllRoleDefinitions() {
        return userDao.getAllRoleDefinitions();
    }

    @Transactional(readOnly = true)
    public List<RoleDefinition> getRoleDefinitionsByUser(User user) {
        return userDao.getRoleDefinitionsByUser(user);
    }

    @Transactional(readOnly = true)
    public RoleDefinition getRoleDefinitionById(Integer id) {
        return userDao.getRoleDefinitionById(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveOrUpdateUser(User user) {
        userDao.saveOrUpdateUser(user);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveOrUpdateRole(Role role) {
        userDao.saveOrUpdateRole(role);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteRole(User user, RoleDefinition roleDefinition) {
        userDao.deleteRole(user, roleDefinition);
    }

}