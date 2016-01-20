package com.bankofbaku.secure;

import com.bankofbaku.dao.UserDao;
import com.bankofbaku.model.Role;
import com.bankofbaku.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Transactional
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        User user = null;
        try {
            user = userDao.getUserByLogin(login);
        } catch (Exception e) {
            throw new UsernameNotFoundException("user not found");
        }

        if (user == null)
            throw new UsernameNotFoundException("user not found");

        return buildUserFromUserEntity(user);
    }

    private SessionUser buildUserFromUserEntity(User user) {
        String username = user.getLogin();
        String password = user.getPassword();
        User.Status status = user.getStatus();
        boolean isAlive = user.getExpired().compareTo(new Date()) >= 0;
        boolean active = status == User.Status.ACTIVE;
        boolean accountNonExpired = isAlive;
        boolean credentialsNonExpired = isAlive;
        boolean accountNonLocked = status != User.Status.LOCKED && isAlive;

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleDefinition()
                    .getName()));
        }

        SessionUser userHolder = new SessionUser(username, password, active,
                accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities);
        return userHolder;
    }
}