package com.taskadapter.webui;

import com.vaadin.flow.component.html.Image;

public class ImageLoader {
    private static final String IMAGES_FOLDER = "images/";

    public static Image getImage(String name) {
        return new Image(IMAGES_FOLDER + name, name);
    }
}
