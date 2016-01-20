package com.bankofbaku.util;

import com.bankofbaku.secure.SessionUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserHelper {
    private final static String anonymousUser = "anonymousUser";

    public static String getSessionUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null)
            return null;
        if (principal instanceof String)
            return ((String) principal).equals(anonymousUser) ? null : (String) principal;
        if (principal instanceof SessionUser)
            return ((SessionUser) principal).getUsername();
        return null;
    }
}
