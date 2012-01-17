package com.taskadapter.connector.msp;

import java.io.File;
import java.util.List;

import com.taskadapter.connector.definition.*;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;

import com.taskadapter.connector.common.AbstractConnector;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class MSPConnector extends AbstractConnector<MSPConfig> implements FileBasedConnector {

    public MSPConnector(MSPConfig config) {
        super(config);
    }

    @Override
    public void updateRemoteIDs(ConnectorConfig config,	SyncResult res, ProgressMonitor monitorIGNORED) {
        if (res.getCreateTasksNumber()==0) {
            return;
        }
        MSPConfig c = (MSPConfig) config;
        String fileName = c.getInputFileName();
        try {
            ProjectFile projectFile = new MSPFileReader().readFile(fileName);
            List<Task> allTasks = projectFile.getAllTasks();
            for (Task mspTask : allTasks) {
                String createdTaskKey = res.getRemoteKey(mspTask.getUniqueID());
                if (createdTaskKey != null) {
                    setFieldIfNotNull(c, FIELD.REMOTE_ID, mspTask, createdTaskKey);
                }
            }

            new MSXMLFileWriter(c).writeProject(projectFile);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private void setFieldIfNotNull(MSPConfig config, FIELD field, Task mspTask, String value) {
        String v = config.getFieldMappedValue(field);
        TaskField f = MSPUtils.getTaskFieldByName(v);
        mspTask.set(f, value);
    }

    private Object getField(MSPConfig config, FIELD field, Task mspTask) {
        String v = config.getFieldMappedValue(field);
        TaskField f = MSPUtils.getTaskFieldByName(v);
        return mspTask.getCurrentValue(f);
    }

    @Override
    public Descriptor getDescriptor() {
        return MSPDescriptor.instance;
    }

    @Override
    public void updateTasksByRemoteIds(List<GTask> tasksFromExternalSystem) {
        String fileName = config.getInputFileName();
        MSXMLFileWriter writer = new MSXMLFileWriter(config);
        try {
            ProjectFile projectFile = new MSPFileReader().readFile(fileName);
            List<Task> allTasks = projectFile.getAllTasks();
            for (GTask gTask : tasksFromExternalSystem) {
                Task mspTask = findTaskByRemoteId(allTasks, gTask.getKey());
                System.out
                        .println("external " + gTask + " for task " + mspTask);
                writer.setTaskFields(projectFile, mspTask, gTask, true);

            }
            writer.writeProject(projectFile);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private Task findTaskByRemoteId(List<Task> mspTasks,
                                    String requiredRemoteId) {
        for (Task gTask : mspTasks) {
            String taskRemoteId = (String) getField(config, FIELD.REMOTE_ID, gTask);
            if (taskRemoteId == null) {
                // not all tasks will have remote IDs
                continue;
            }
            if (taskRemoteId.equals(requiredRemoteId)) {
                return gTask;
            }
        }
        return null;
    }

    @Override
    public boolean fileExists() {
        return (new File(config.getOutputFileName())).exists();
    }

    @Override
    public String getAbsoluteOutputFileName() {
        return config.getOutputFileName();
    }

    @Override
    public void validateCanUpdate() throws ValidationException {
        if (config.getInputFileName().toLowerCase().endsWith(MSPFileReader.MPP_SUFFIX_LOWERCASE)) {
            throw new ValidationException("The Microsoft Project 'Input File Name' you provided ends with \"" +
                    MSPFileReader.MPP_SUFFIX_LOWERCASE + "\"."
                    + "\nTask Adapter can't write MPP files."
                    + "\nPlease replace the MPP file name with XML if you want to use \"Update\" operation.");
        }

    }
}
