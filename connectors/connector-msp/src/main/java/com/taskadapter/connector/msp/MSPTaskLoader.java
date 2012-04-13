package com.taskadapter.connector.msp;

import com.taskadapter.connector.common.AbstractTaskLoader;
import com.taskadapter.model.GTask;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

import java.io.FileNotFoundException;
import java.util.List;

public class MSPTaskLoader extends AbstractTaskLoader<MSPConfig> {

    private MSTaskToGenericTaskConverter converter;
    private MSPFileReader fileReader;

    public MSPTaskLoader() {
        converter = new MSTaskToGenericTaskConverter();
        fileReader = new MSPFileReader();
    }

    public MSPTaskLoader(MSPFileReader fileReader, MSTaskToGenericTaskConverter converter) {
        this.fileReader = fileReader;
        this.converter = converter;
    }

    @Override
    public List<GTask> loadTasks(MSPConfig config) throws Exception {
        ProjectFile projectFile;
        try {
            projectFile = fileReader.readFile(config.getInputFileName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("MSP: Can't find file with name \"" + config.getInputFileName() + "\".");
        }

        List<Task> mspTasks = projectFile.getAllTasks();
        mspTasks = skipRootNodeIfPresent(mspTasks);
        return loadTasks(projectFile, config, mspTasks);
    }


    /**
     * MSP XML file can have a root-level task with outline=0 - this is
     * a grouping task for everything (like "project root"), which should not be included in the tasks
     * list.
     *
     * @return flat list of tasks (not a tree!)
     */
    List<Task> skipRootNodeIfPresent(List<Task> mspTasks) {
//        if (mspTasks.get(0).getParentTask() == null){
//            return mspTasks.subList(1, mspTasks.size());
//        }
        if ((mspTasks != null) && (!mspTasks.isEmpty())
                && mspTasks.get(0).getOutlineLevel().equals(0)) {
            mspTasks = mspTasks.subList(1, mspTasks.size());
        }
        return mspTasks;
    }

    List<GTask> loadTasks(ProjectFile project, MSPConfig config, List<Task> mspTasks) {
        converter.setConfig(config);
        converter.setHeader(project.getProjectHeader());
        converter.setMappings(config.getFieldsMapping());
        // XXX add fieldMappings to the params!
        return converter.convertToGenericTaskList(mspTasks);
    }

    @Override
    public GTask loadTask(MSPConfig config, String taskKey) {
        throw new RuntimeException("not implemented");
    }


}
