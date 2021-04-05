package com.taskadapter.webui.results;

import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.DecodedTaskError;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExportResultFormat {
    private String resultId;
    private ConfigId configId;
    private String configLabel;
    private String from;
    private String to;
    private String targetFileName;
    private int updatedTasksNumber;
    private int createdTasksNumber;
    private List<String> generalErrors;
    private List<DecodedTaskError> taskErrors;
    private Date dateStarted;
    private int timeTookSeconds;

    public ExportResultFormat(String resultId, ConfigId configId, String configLabel, String from, String to,
                              String targetFileName, int updatedTasksNumber, int createdTasksNumber,
                              List<String> generalErrors, List<DecodedTaskError> taskErrors, Date dateStarted,
                              int timeTookSeconds) {
        this.resultId = resultId;
        this.configId = configId;
        this.configLabel = configLabel;
        this.from = from;
        this.to = to;
        this.targetFileName = targetFileName;
        this.updatedTasksNumber = updatedTasksNumber;
        this.createdTasksNumber = createdTasksNumber;
        this.generalErrors = generalErrors;
        this.taskErrors = taskErrors;
        this.dateStarted = dateStarted;
        this.timeTookSeconds = timeTookSeconds;
    }

    public boolean hasErrors() {
        return !generalErrors.isEmpty() || !taskErrors.isEmpty();
    }

    public boolean isSuccess() {
        return !hasErrors();
    }

    public String getResultId() {
        return resultId;
    }

    public ConfigId getConfigId() {
        return configId;
    }

    public String getConfigLabel() {
        return configLabel;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public int getUpdatedTasksNumber() {
        return updatedTasksNumber;
    }

    public int getCreatedTasksNumber() {
        return createdTasksNumber;
    }

    public List<String> getGeneralErrors() {
        return generalErrors;
    }

    public List<DecodedTaskError> getTaskErrors() {
        return taskErrors;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public int getTimeTookSeconds() {
        return timeTookSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportResultFormat that = (ExportResultFormat) o;
        return updatedTasksNumber == that.updatedTasksNumber && createdTasksNumber == that.createdTasksNumber && timeTookSeconds == that.timeTookSeconds && Objects.equals(resultId, that.resultId) && Objects.equals(configId, that.configId) && Objects.equals(configLabel, that.configLabel) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(targetFileName, that.targetFileName) && Objects.equals(generalErrors, that.generalErrors) && Objects.equals(taskErrors, that.taskErrors) && Objects.equals(dateStarted, that.dateStarted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultId, configId, configLabel, from, to, targetFileName, updatedTasksNumber, createdTasksNumber, generalErrors, taskErrors, dateStarted, timeTookSeconds);
    }
}
