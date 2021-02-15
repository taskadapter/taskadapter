package com.taskadapter.webui;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;

public class CookiesManager {
    // make the cookie visible to the whole application
    private static String cookieApplicationPath = "/";

    public static void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(2592000);//one month
        cookie.setPath(cookieApplicationPath);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static void expireCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath(cookieApplicationPath);
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static String getCookie(String cookieName) {
        VaadinRequest currentRequest = VaadinService.getCurrentRequest();
        // it can be null when running some tests
        if (currentRequest == null) {
            return null;
        }
        Cookie[] cookies = currentRequest.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName))
                return cookie.getValue();
        }
        return null;
    }
}
