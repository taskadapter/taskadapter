package com.taskadapter.webui;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.model.GTask;
import com.taskadapter.webui.action.ConfirmationPage;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ActionPage extends Page {
    protected final VerticalLayout mainPanel;
    protected final Connector connectorFrom;
    protected final Connector connectorTo;

    protected ProgressIndicator loadProgress = new ProgressIndicator();
    protected ProgressIndicator saveProgress = new ProgressIndicator();
    protected List<GTask> loadedTasks;
    private ConfirmationPage confirmationPage;

    public ActionPage(Connector connectorFrom, Connector connectorTo) {
        this.connectorFrom = connectorFrom;
        this.connectorTo = connectorTo;
        mainPanel = new VerticalLayout();
        setCompositionRoot(mainPanel);
        buildInitialPage();
    }

    protected abstract void saveData();

    protected abstract void loadData();

    private void buildInitialPage() {
        String text = getInitialText();
        Label label = new Label(text);
        label.setContentMode(Label.CONTENT_XHTML);
        mainPanel.addComponent(label);
        Button goButton = new Button("Go");
        goButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                synchronized (getApplication()) {
                    buildLoadingPage();
                }
                new LoadWorker().start();
            }
        });
        mainPanel.addComponent(goButton);
    }

    protected abstract String getInitialText();

    protected abstract String getNoDataLoadedText();

    protected void buildLoadingPage() {
        mainPanel.removeAllComponents();
        loadProgress = new ProgressIndicator();
        loadProgress.setIndeterminate(true);
        loadProgress.setPollingInterval(200);
        mainPanel.addComponent(loadProgress);
        String labelText = "loading data from " + connectorFrom.getConfig().getSourceLocation() + " (" + connectorFrom.getDescriptor().getLabel() + ")";
        mainPanel.addComponent(new Label(labelText));
    }

    protected void showAfterDataLoaded() {
        loadProgress.setEnabled(false);
        if (loadedTasks == null || loadedTasks.isEmpty()) {
            mainPanel.addComponent(new Label(getNoDataLoadedText()));
        } else {
            buildConfirmationUI();
        }
    }

    private class LoadWorker extends Thread {
        @Override
        public void run() {
            loadData();
            // must synchronize changes over application
            synchronized (getApplication()) {
                showAfterDataLoaded();
            }
        }
    }

    private class SaveWorker extends Thread {
        @Override
        public void run() {
            saveData();
            // must synchronize changes over application
            synchronized (getApplication()) {
                showAfterExport();
            }
        }
    }

    private void showAfterExport() {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("DONE"));
    }

    protected void buildConfirmationUI() {
        confirmationPage = new ConfirmationPage(loadedTasks, connectorTo, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        mainPanel.addComponent(confirmationPage);
        mainPanel.setExpandRatio(confirmationPage, 1f); // use all available space
    }

    protected void save() {
        loadedTasks = confirmationPage.getSelectedRootLevelTasks();

        if (!loadedTasks.isEmpty()) {
            saveProgress = new ProgressIndicator();
            saveProgress.setIndeterminate(false);
            saveProgress.setEnabled(true);
            saveProgress.setCaption("Saving to " + connectorTo.getConfig().getTargetLocation());
            mainPanel.removeAllComponents();
            mainPanel.addComponent(saveProgress);

            new SaveWorker().start();
        }
    }

}
