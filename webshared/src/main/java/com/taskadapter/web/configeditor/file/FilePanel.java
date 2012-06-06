package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.vaadin.ui.Panel;

public abstract class FilePanel extends Panel {
    public abstract void refreshConfig(MSPConfig config);

    public FilePanel(String panelCaption) {
        super(panelCaption);
    }
}
