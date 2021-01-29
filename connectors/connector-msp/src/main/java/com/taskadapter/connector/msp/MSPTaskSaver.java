package com.taskadapter.connector.msp;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.RelationUtils;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityPersistenseException;
import com.taskadapter.connector.msp.write.RealWriter;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Precedes$;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class MSPTaskSaver {

    private static final Logger logger = LoggerFactory
            .getLogger(MSPTaskSaver.class);
    private final SaveResultBuilder result = new SaveResultBuilder();

    private MsXmlFileWriter writer;
    private FileSetup setup;

    public MSPTaskSaver(FileSetup setup, Iterable<FieldRow<?>> rows) {
        this.setup = setup;
        this.writer = new MsXmlFileWriter(rows);
    }

    public SaveResult saveData(List<GTask> tasks) throws ConnectorException {
        saveIssues(tasks);

        final List<GRelation> relations = RelationUtils.convertRelationIds(tasks, result);
        saveRelations(relations);

        return result.getResult();
    }

    /**
     * This method allows saving data to MSP file while keeping tasks ids.
     */
    private void saveIssues(List<GTask> tasks) throws ConnectorException {
        try {
            final String resultFile = writer.write(
                    setup.targetFile(), result, tasks, false);
            result.setTargetFileAbsolutePath(resultFile);
        } catch (Exception e) {
            throw MSPExceptions.convertException(e);
        }
    }

    // TODO this is a hack until this class is refactored to follow the new API.
    // have to re-open the file, which we JUST CREATED!
    private void saveRelations(List<GRelation> relations) {
        MSPFileReader fileReader = new MSPFileReader();
        try {
            ProjectFile projectFile = fileReader.readFile(setup.targetFile());
            for (GRelation relation : relations) {
                if (relation.type().equals(Precedes$.MODULE$)) {
                    Long intKey = relation.relatedTaskId().id();
                    Long sourceKey = relation.taskId().id();
                    Task relatedTask = projectFile.getTaskByID(intKey.intValue());
                    Task sourceTask = projectFile.getTaskByID(sourceKey.intValue());

                    Duration delay;
//                    if (relation.getDelay() != null) {
//                        delay = Duration.getInstance(relation.getDelay(),
//                                TimeUnit.DAYS);
//                    } else {
                        delay = Duration.getInstance(0, TimeUnit.DAYS);
//                    }
                    relatedTask.addPredecessor(sourceTask,
                            RelationType.FINISH_START, delay);
                } else {
                    logger.error("save relations for MSP: unknown type: " + relation.type());
                    result.addGeneralError(new UnsupportedRelationType(relation.type()));
                }
            }

            RealWriter.writeProject(setup.targetFile(), projectFile);
        } catch (Throwable e) {
            result.addGeneralError(new EntityPersistenseException(
                    "Can't create Tasks Relations (" + e.toString() + ")"));
            e.printStackTrace();
        }
    }

}
