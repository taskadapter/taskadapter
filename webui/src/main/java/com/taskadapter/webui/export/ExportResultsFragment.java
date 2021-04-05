package com.taskadapter.webui.export;

import com.google.common.base.Strings;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.web.uiapi.DecodedTaskError;
import com.taskadapter.webui.results.ExportResultFormat;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.taskadapter.webui.Page.message;

public class ExportResultsFragment {
    private static final Logger log = LoggerFactory.getLogger(ExportResultsFragment.class);

    private final boolean showFilePath;

    public ExportResultsFragment(boolean showFilePath) {
        this.showFilePath = showFilePath;
    }

    private static String quot(String str) {
        return str.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Shows task export result.
     */
    public Component showExportResult(ExportResultFormat result) {
        var donePanel = new VerticalLayout();
        donePanel.setWidth("900px");
        donePanel.setClassName("export-panel");
        // TODO format inside MESSAGES formatter, not here.
        var time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance().getTime());
        var label = new HtmlLabel(message("export.exportCompletedOn", time));
        donePanel.add(label);

        donePanel.add(createdExportResultLabel(message("export.from"), result.getFrom()));
        donePanel.add(createdExportResultLabel(message("export.to"), result.getTo()));

        if (!Strings.isNullOrEmpty(result.getTargetFileName())) {
            if (showFilePath) {
                var flabel = new HtmlLabel(message("export.pathToExportFile", result.getTargetFileName()));
                donePanel.add(flabel);
            }
            donePanel.add(createDownloadButton(result.getTargetFileName()));
        }
        donePanel.add(createdExportResultLabel(message("export.createdTasks"), String.valueOf(result.getCreatedTasksNumber())));
        donePanel.add(createdExportResultLabel(message("export.updatedTasks"), String.valueOf(result.getUpdatedTasksNumber()) + "<br/><br/>"));


        addErrors(donePanel, result.getGeneralErrors(), result.getTaskErrors());

        var ui = new VerticalLayout();
        ui.add(donePanel);

        return ui;
    }

    /**
     * Adds connector errors into the output.
     *
     * @param container     * error container.
     * @param generalErrors * list of general errors.
     * @param taskErrors    * errors for each task.
     */
    private void addErrors(HasComponents container, List<String> generalErrors, List<DecodedTaskError> taskErrors) {
        if (generalErrors.isEmpty() && taskErrors.isEmpty()) {
            return;
        }
        var label = new H2(message("exportResults.thereWereErrors"));
        container.add(label);

        if (!generalErrors.isEmpty()) {
            var generalErrorText = new StringBuffer();
            generalErrors.forEach(error -> {
                generalErrorText.append(quot(error) + "<br/>\n");
            });
            var generalErrorLabel = new HtmlLabel(generalErrorText.toString());
            generalErrorLabel.addClassName("failure");
            container.add(generalErrorLabel);
        }

        if (!taskErrors.isEmpty()) {
            var taskErrorsLabel = new H2(message("exportResults.taskErrors"));
            container.add(taskErrorsLabel);

            taskErrors.forEach(error -> {
                var taskSourceId = error.sourceSystemTaskId;
                var errorText = "Task source id " + taskSourceId + "<br/>" + error.connector2ErrorText;
                var errorTextLabel = new HtmlLabel(errorText);
                errorTextLabel.addClassName("failure");
                container.add(errorTextLabel);
            });
        }
    }

    private static HorizontalLayout createdExportResultLabel(String labelName, String labelValue) {
        var lName = new HtmlLabel("<strong>" + labelName + "</strong>");
        lName.setWidth("120px");
        var lValue = new HtmlLabel("<em>" + labelValue + "</em>");
        var layout = new HorizontalLayout();
        layout.add(lName);
        layout.add(lValue);
        layout.addClassName("export-result");
        return layout;
    }

    /**
     * Creates "download file" button.
     *
     * @param targetFileAbsolutePath target path.
     */
    private static Button createDownloadButton(String targetFileAbsolutePath) {
        var downloadButton = new Button(message("export.downloadFile"));
        var file = new File(targetFileAbsolutePath);
        // TODO 14 fix download links
//    var resource = new FileDownloadResource(file)
//    var downloader = new FileDownloader(resource)
//    downloader.extend(downloadButton)
        return downloadButton;
    }
}
