package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.vaadin.ui.GridLayout;

public abstract class FilePanel extends GridLayout {
    public abstract void refreshConfig(MSPConfig config);
    public abstract String getInputFileName();
    public abstract String getOutputFileName();
}
