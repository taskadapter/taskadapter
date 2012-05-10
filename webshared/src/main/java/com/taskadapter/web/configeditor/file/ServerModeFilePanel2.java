package com.taskadapter.web.configeditor.file;

import com.taskadapter.connector.msp.MSPConfig;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

/**
 * @author Alexander Kulik
 */
public class ServerModeFilePanel2 extends FilePanel {
    
    public ServerModeFilePanel2(ServerModelFilePanelPresenter presenter) {
        presenter.setView(this);
        buildUI();
    }

    private void buildUI() {
        removeAllComponents();

        addComponent(createUploadPanel());
        addComponent(createDownloadPanel());
    }

    private Layout createUploadPanel() {
        HorizontalLayout layout = new HorizontalLayout();

        layout.addComponent(createSelectComboBox());
        layout.addComponent(createUploadControl());

        return layout;
    }

    private Component createUploadControl() {

    }

    private Component createSelectComboBox() {

    }

    private Layout createDownloadPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        return layout;
    }

    @Override
    public void refreshConfig(MSPConfig config) {

    }

    @Override
    public String getInputFileName() {
        return null;
    }

    @Override
    public String getOutputFileName() {
        return null;
    }
}
