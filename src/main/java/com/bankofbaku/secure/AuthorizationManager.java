package com.bankofbaku.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationManager {

    private final static Logger logger = LoggerFactory
            .getLogger(AuthorizationManager.class);

    public static SessionUser getSessionUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof SessionUser)
            return (SessionUser) principal;
        else {
            logger.debug(principal == null ? null : principal.toString());
            return null;
        }
    }
}
