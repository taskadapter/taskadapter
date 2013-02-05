package com.taskadapter.webui;

import com.vaadin.server.VaadinService;

import javax.servlet.http.Cookie;

public class CookiesManager {

    public static void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(2592000);//one month
        cookie.setPath("/ta");
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static void expireCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/ta");
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static String getCookie(String cookieName) {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
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
