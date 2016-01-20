package com.bankofbaku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/resources/js/i18n/**")
public class Jsi18nController {
    @RequestMapping(method = RequestMethod.GET)
    public String i18nTest(final HttpServletRequest request) {
        String path = (String) request
                .getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String pattern = (String) request
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(pattern, path);
        int jsPosition = finalPath.lastIndexOf(".js");

        String forward = "forward:";

        if (jsPosition == finalPath.length() - 3)
            forward += "/resources/js/i18n/"
                    + finalPath.substring(0, jsPosition) + ".jsp";
        else
            forward += "/resources/js/i18n/blank.jsp";

        return forward;
    }
}