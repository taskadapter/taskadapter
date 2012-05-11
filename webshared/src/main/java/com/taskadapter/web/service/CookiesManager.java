package com.taskadapter.web.service;

import org.vaadin.browsercookies.BrowserCookies;

import java.util.Date;

public class CookiesManager {
    private BrowserCookies cookies;

    public CookiesManager() {
        cookies = new BrowserCookies();

        cookies.addListener(new BrowserCookies.UpdateListener() {
            public void cookiesUpdated(BrowserCookies bc) {
                cookies.setData(bc);
            }
        });
    }

    public BrowserCookies getCookiesComponent() {
        return cookies;
    }

    public void setCookie(String cookieName, String cookieValue, Date cookieExpire) {
        cookies.setCookie(cookieName, cookieValue, cookieExpire);
    }

    public void expireCookie(String cookieName) {
        cookies.setCookie(cookieName, "", new Date());
    }

    public String getCookie(String cookieName) {
        return cookies.getCookie(cookieName);
    }
}
