package com.taskadapter.webui;

import com.vaadin.server.ThemeResource;

public class ImageLoader {
    private static final String IMAGES_FOLDER = "img/";

    public static ThemeResource getImage(String name) {
        return new ThemeResource(IMAGES_FOLDER + name);
    }
}
