package com.taskadapter.webui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

@Theme("mytheme")
public class DummyUI extends UI implements PageContainer {

    @Override
    public void setPageContent(Component content) {
        setContent(content);
    }

    @Override
    protected void init(VaadinRequest request) {
        /*
         * Nothing to do. Really. All is done. Go away and poke vaadin
         * developers.
         */
    }

}
