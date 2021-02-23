package com.taskadapter.webui.pages;

import com.taskadapter.model.GTask;
import com.taskadapter.reporting.ErrorReporter;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.ExportCategory$;
import com.taskadapter.webui.WebProgressMonitorWrapper;
import com.taskadapter.webui.export.ConfirmExportFragment;
import com.taskadapter.webui.export.ExportResultsFragment;
import com.taskadapter.webui.results.ExportResultStorage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

import static com.taskadapter.webui.Page.message;

public class ExportHelper {
    private final ExportResultStorage exportResultStorage;
    private final Runnable onDone;
    private final boolean showFilePath;
    private final VerticalLayout layout;
    private final UISyncConfig config;
    private final ConfigOperations configOps;

    public ExportHelper(ExportResultStorage exportResultStorage, Runnable onDone, boolean showFilePath, VerticalLayout layout, UISyncConfig config, ConfigOperations configOps) {
        this.exportResultStorage = exportResultStorage;
        this.onDone = onDone;
        this.showFilePath = showFilePath;
        this.layout = layout;
        this.config = config;
        this.configOps = configOps;
    }

    public void onTasksLoaded(List<GTask> tasks) {
        var dataSourceLabel = config.getConnector1().getConnectorTypeId();
        EventTracker.trackEvent(ExportCategory$.MODULE$, "loaded_tasks", dataSourceLabel, tasks.size());

        if (tasks.isEmpty()) {
            showNoDataLoaded();
        } else {
            showConfirmation(tasks);
        }
    }

    /**
     * Shows "no data loaded" content.
     */
    public void showNoDataLoaded() {
        var res = new VerticalLayout();
        var msg = new Label(message("export.noDataWasLoaded"));
        msg.setWidth("800px");
        var backButton = new Button(message("button.back"),
                e -> onDone.run());
        res.add(msg);
        res.add(backButton);
        setContent(res);
    }

    private void showConfirmation(List<GTask> tasks) {
        var confirmationComponent = ConfirmExportFragment.render(configOps, config, tasks, new ConfirmExportFragment.Callback() {
            @Override
            public void onTasks(List<GTask> selectedTasks) {
                performExport(selectedTasks);
            }

            @Override
            public void onCancel() {
                onDone.run();
            }
        });
        setContent(confirmationComponent);
    }

    private void performExport(List<GTask> selectedTasks) {
        if (selectedTasks.isEmpty()) {
            Notification.show(message("action.pleaseSelectTasks"));
            return;
        }
        var saveProgress = SyncActionComponents.renderSaveIndicator(config.getConnector2());

        var progressBarCaption = "Saving " + selectedTasks.size() + " tasks to " + config.getConnector2().getDestinationLocation();
        var wrapper = new WebProgressMonitorWrapper(saveProgress, progressBarCaption, selectedTasks.size());
        setContent(wrapper);

        new Thread(() -> {

            var saveResult = config.saveTasks(selectedTasks, wrapper);
            ExportResultsLogger.log(saveResult, "Export completed.");
            exportResultStorage.store(saveResult);
            if (saveResult.hasErrors()) {
                ErrorReporter.reportIfAllowed(config, saveResult);
            }
            var targetLabel = config.getConnector2().getConnectorTypeId();
            var sourceAndTarget = config.getConnector1().getConnectorTypeId() + " - " + targetLabel;
            var exportResult = new ExportResultsFragment(showFilePath).showExportResult(saveResult);
            EventTracker.trackEvent(ExportCategory$.MODULE$, "finished_export", sourceAndTarget);
            EventTracker.trackEvent(ExportCategory$.MODULE$, "finished_saving_tasks", targetLabel);
            EventTracker.trackEvent(ExportCategory$.MODULE$, "created_tasks", targetLabel, saveResult.createdTasksNumber());
            EventTracker.trackEvent(ExportCategory$.MODULE$, "updated_tasks", targetLabel, saveResult.updatedTasksNumber());
            EventTracker.trackEvent(ExportCategory$.MODULE$, "tasks_with_errors", targetLabel, saveResult.taskErrors().size());

            var okButton = new Button(message("button.ok"),
                    event -> onDone.run());

            setContent(new VerticalLayout(exportResult, okButton));
        }).start();
    }

    private void setContent(Component comp) {
        layout.getUI().get().access(() -> {
            layout.removeAll();
            layout.add(comp);
        });
    }
}
