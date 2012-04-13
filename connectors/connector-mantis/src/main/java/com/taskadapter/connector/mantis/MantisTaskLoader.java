package com.taskadapter.connector.mantis;

import com.taskadapter.connector.common.AbstractTaskLoader;
import com.taskadapter.model.GTask;
import org.mantis.ta.MantisManager;
import org.mantis.ta.beans.IssueData;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MantisTaskLoader extends AbstractTaskLoader<MantisConfig> {

    @Override
    public List<GTask> loadTasks(MantisConfig config) throws Exception {
        List<GTask> rows;
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            List<IssueData> issues = mgr.getIssuesByProject(new BigInteger(config.getProjectKey()));
            rows = convertToGenericTasks(issues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    @Override
    public GTask loadTask(MantisConfig config, String taskKey) {
        try {
            MantisManager mgr = MantisManagerFactory.createMantisManager(config.getServerInfo());

            IssueData issue = mgr.getIssueById(new BigInteger(taskKey));
            return MantisDataConverter.convertToGenericTask(issue);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private List<GTask> convertToGenericTasks(List<IssueData> issues) {
        List<GTask> result = new ArrayList<GTask>(issues.size());
        for (IssueData issue : issues) {
            GTask task = MantisDataConverter.convertToGenericTask(issue);
            result.add(task);
        }
        return result;
    }

}
