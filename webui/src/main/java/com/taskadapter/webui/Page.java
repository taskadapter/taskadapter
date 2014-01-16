package com.taskadapter.webui;

import com.taskadapter.web.data.Messages;

public abstract class Page {
    public static final String BUNDLE_NAME = "com.taskadapter.webui.data.messages";
    // TODO !! do not create instances in every single page!
    public static final Messages MESSAGES = new Messages(BUNDLE_NAME);
}
