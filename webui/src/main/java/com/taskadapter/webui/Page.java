package com.taskadapter.webui;

import com.vaadin.ui.CustomComponent;

/**
 * @author Alexey Skorokhodov
 */
public abstract class Page extends  CustomComponent {

    abstract public String getNavigationPanelTitle();
}
