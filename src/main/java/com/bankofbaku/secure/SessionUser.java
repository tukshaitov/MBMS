package com.bankofbaku.secure;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SessionUser extends User {

    private static final long serialVersionUID = 1L;

    public SessionUser(String username, String password,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public SessionUser(String username, String password, boolean enabled,
                       boolean accountNonExpired, boolean credentialsNonExpired,
                       boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);
    }

}
