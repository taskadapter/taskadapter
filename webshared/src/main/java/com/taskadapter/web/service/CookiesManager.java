package com.taskadapter.web.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class CookiesManager {
    HttpServletRequest request;
    HttpServletResponse response;


    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(2592000);//one month
        response.addCookie(cookie);
    }

    public void expireCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String getCookie(String cookieName) {
        int foundCookieIndex = -1;
        if (request != null) {
            for (int i = 0; i < request.getCookies().length; i++) {
                if (request.getCookies()[i].getName().equals(cookieName))
                    foundCookieIndex = i;
            }
        }

        if (foundCookieIndex == -1) {
            return null;
        } else {
            return request.getCookies()[foundCookieIndex].getValue();
        }
    }
}
