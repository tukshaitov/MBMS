package com.bankofbaku.controller;

import com.bankofbaku.secure.AuthorizationManager;
import com.bankofbaku.util.IResponse;
import com.bankofbaku.util.IResponse.Status;
import com.bankofbaku.util.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class Application {
    static final Logger logger = LoggerFactory.getLogger(Application.class);

    @RequestMapping("/application.htm")
    public String application() {
        if (AuthorizationManager.getSessionUser() != null)
            return "application";
        else
            return "login";
    }

    @RequestMapping("/exit.json")
    @ResponseBody
    public IResponse exit(HttpServletRequest request, @RequestParam(value = "isSave", required = false) final Boolean isSave) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();
        return new SimpleResponse(Status.OK, "");
    }
}
