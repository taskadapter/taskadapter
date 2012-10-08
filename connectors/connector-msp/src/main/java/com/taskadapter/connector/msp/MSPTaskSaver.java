package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityPersistenseException;
import com.taskadapter.connector.msp.write.MSXMLFileWriter;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelation.TYPE;
import com.taskadapter.model.GTask;
import net.sf.mpxj.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class MSPTaskSaver extends AbstractTaskSaver<MSPConfig> {

    private static final Logger logger = LoggerFactory.getLogger(MSPTaskSaver.class);

    private MSXMLFileWriter writer;

    // TODO !!! unused param
    public MSPTaskSaver(MSPConfig config, Mappings mappings) {
        super(config);
        this.writer = new MSXMLFileWriter(config);
    }

    @Override
    protected void save(String parentTaskKey, List<GTask> tasks) throws ConnectorException {
        saveData(tasks, false);
        List<GTask> newTaskList = TreeUtils.buildFlatListFromTree(tasks);
        List<GRelation> relations = buildNewRelations(newTaskList);
        saveRelations(relations);
    }

    // TODO this method is always called with "false" ONLY

    /**
     * This method allows saving data to MSP file while keeping tasks ids.
     * @throws ConnectorException 
     */
    private void saveData(List<GTask> tasks, boolean keepTaskId) throws ConnectorException {
        try {
            String resultFile = writer.write(result, tasks, keepTaskId);
            result.setTargetFileAbsolutePath(resultFile);
        } catch (IOException e) {
            throw MSPExceptions.convertException(e);
        }
    }

    @Override
    protected Object convertToNativeTask(GTask task) {
        // TODO Auto-generated method stub
        return null;
    }

    // TODO refactoring: convert MSXMLFileWriter to use the new TaskWriter API (already used by Redmine and Jira)
    // https://www.hostedredmine.com/issues/22490
    @Override
    protected String submitTask(GTask task, Object nativeTask) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected GTask createTask(Object nativeTask) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) {
        // TODO Auto-generated method stub

    }

    @Override
    // TODO this is a hack until this class is refactored to follow the new API.
    // have to re-open the file, which we JUST CREATED!
    protected void saveRelations(List<GRelation> relations) {
        MSPFileReader fileReader = new MSPFileReader();
        try {
            ProjectFile projectFile = fileReader.readFile(config.getOutputAbsoluteFilePath());
            for (GRelation relation : relations) {
                if (relation.getType().equals(TYPE.precedes)) {
                    Integer intKey = Integer.parseInt(relation
                            .getRelatedTaskKey());
                    Integer sourceKey = Integer.parseInt(relation.getTaskKey());
                    Task relatedTask = projectFile.getTaskByID(intKey);
                    Task sourceTask = projectFile.getTaskByID(sourceKey);

                    Duration delay;
                    if (relation.getDelay() != null) {
                        delay = Duration.getInstance(relation.getDelay(),
                                TimeUnit.DAYS);
                    } else {
                        delay = Duration.getInstance(0, TimeUnit.DAYS);
                    }
                    relatedTask.addPredecessor(sourceTask,
                            RelationType.FINISH_START, delay);
                } else {
                    logger.error("save relations for MSP: unknown type: " + relation.getType());
                    errors.addGeneralError(new UnsupportedRelationType(relation.getType()));
                }
            }

            writer.writeProject(projectFile);
        } catch (MPXJException e) {
            errors.addGeneralError(new EntityPersistenseException("Can't create Tasks Relations (" + e.toString() + ")"));
            e.printStackTrace();
        } catch (IOException e) {
            errors.addGeneralError(new EntityPersistenseException("Can't create Tasks Relations (" + e.toString() + ")"));
            e.printStackTrace();
        } catch (Throwable e) {
            errors.addGeneralError(new EntityPersistenseException("Can't create Tasks Relations (" + e.toString() + ")"));
            e.printStackTrace();
        }
    }

}
