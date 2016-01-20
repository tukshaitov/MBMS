package com.bankofbaku.dao;

import com.bankofbaku.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/config/servlet-context.xml", "file:src/main/webapp/WEB-INF/config/security-context.xml"})
public class UserDaoTest {

    static Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

    @Autowired
    private UserDao userDao;

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> list = userDao.getAllUsers();
        logger.info(list.toString());
    }
}