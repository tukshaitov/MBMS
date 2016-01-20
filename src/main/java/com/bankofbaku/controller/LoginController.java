package com.bankofbaku.controller;

import com.bankofbaku.service.UserService;
import com.bankofbaku.util.IResponse;
import com.bankofbaku.util.IResponse.Status;
import com.bankofbaku.util.SimpleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Controller(value = "loginController")
public class LoginController {

    @Autowired
    @Qualifier("authenticationManager")
    AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionLocaleResolver localeResolver;

    @RequestMapping(value = "/secure/logout.htm", method = RequestMethod.GET)
    public
    @ResponseBody
    IResponse logout(HttpServletRequest request) {
        request.getSession().invalidate();
        final boolean logOut = true;
        return new SimpleResponse(Status.OK, "") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unused")
            public boolean isLogOut() {
                return logOut;
            }
        };
    }

    @RequestMapping(value = "/login.json", method = RequestMethod.POST)
    @ResponseBody
    public IResponse login(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam("j_username") String login,
                           @RequestParam("j_password") String password,
                           @RequestParam("j_language") String language) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

        boolean isAuthenticated;
        try {
            Authentication auth = authenticationManager.authenticate(token);
            getContext().setAuthentication(auth);
            isAuthenticated = auth.isAuthenticated();

        } catch (Exception e) {
            isAuthenticated = false;
        }

        final boolean loggedIn = isAuthenticated;

        if (loggedIn) {
            Locale locale = new Locale(language);
            localeResolver.setLocale(request, response, locale);
        }

        return new SimpleResponse(Status.OK, "") {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unused")
            public boolean isLoggedIn() {
                return loggedIn;
            }
        };

    }
}