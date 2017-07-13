package com.taskadapter.connector.msp;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.RelationUtils;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityPersistenseException;
import com.taskadapter.connector.msp.write.MSXMLFileWriter;
import com.taskadapter.connector.msp.write.RealWriter;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelation.TYPE;
import com.taskadapter.model.GTask;
import net.sf.mpxj.Duration;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public final class MSPTaskSaver {

    private static final Logger logger = LoggerFactory
            .getLogger(MSPTaskSaver.class);
    private final TaskSaveResultBuilder result = new TaskSaveResultBuilder();
    private final MSPConfig config;

    private MSXMLFileWriter writer;

    public MSPTaskSaver(MSPConfig config, Iterable<FieldRow> rows) {
        this.config = config;
        this.writer = new MSXMLFileWriter(rows);
    }

    public TaskSaveResult saveData(List<GTask> tasks) throws ConnectorException {
        saveIssues(tasks);

        final List<GRelation> relations = RelationUtils.convertRelationIds(
                tasks, result);
        saveRelations(relations);

        return result.getResult();
    }

    // TODO this method is always called with "false" ONLY

    /**
     * This method allows saving data to MSP file while keeping tasks ids.
     */
    private void saveIssues(List<GTask> tasks) throws ConnectorException {
        try {
            final String resultFile = writer.write(
                    config.getOutputAbsoluteFilePath(), result, tasks, false);
            result.setTargetFileAbsolutePath(resultFile);
        } catch (IOException e) {
            throw MSPExceptions.convertException(e);
        }
    }

    // TODO this is a hack until this class is refactored to follow the new API.
    // have to re-open the file, which we JUST CREATED!
    private void saveRelations(List<GRelation> relations) {
        MSPFileReader fileReader = new MSPFileReader();
        try {
            String outputAbsoluteFilePath = config.getOutputAbsoluteFilePath();
            ProjectFile projectFile = fileReader.readFile(outputAbsoluteFilePath);
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
                    logger.error("save relations for MSP: unknown type: "
                            + relation.getType());
                    result.addGeneralError(new UnsupportedRelationType(relation
                            .getType()));
                }
            }

            RealWriter.writeProject(outputAbsoluteFilePath, projectFile);
        } catch (Throwable e) {
            result.addGeneralError(new EntityPersistenseException(
                    "Can't create Tasks Relations (" + e.toString() + ")"));
            e.printStackTrace();
        }
    }

}
