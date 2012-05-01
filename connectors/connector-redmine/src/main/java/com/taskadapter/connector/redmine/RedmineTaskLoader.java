package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractTaskLoader;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTask;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.RedmineManager.INCLUDE;
import org.redmine.ta.RedmineSecurityException;
import org.redmine.ta.beans.Issue;

import java.util.ArrayList;
import java.util.List;

public class RedmineTaskLoader extends AbstractTaskLoader<RedmineConfig> {

    @Override
    public List<GTask> loadTasks(RedmineConfig config) throws Exception {
        List<GTask> rows;
        try {
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(config.getServerInfo());

            List<Issue> issues = mgr.getIssues(config.getProjectKey(),
                    config.getQueryId(), INCLUDE.relations);
            rows = convertToGenericTasks(config, issues);
        } catch (RedmineSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    @Override
    public GTask loadTask(RedmineConfig config, String taskKey) {
        try {
            WebServerInfo serverInfo = config.getServerInfo();
            RedmineManager mgr = RedmineManagerFactory
                    .createRedmineManager(serverInfo);

            Integer intKey = Integer.parseInt(taskKey);
            Issue issue = mgr.getIssueById(intKey, INCLUDE.relations);
            RedmineDataConverter converter = new RedmineDataConverter(config);
            return converter.convertToGenericTask(issue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<GTask> convertToGenericTasks(RedmineConfig config,
                                              List<Issue> issues) {
        List<GTask> result = new ArrayList<GTask>(issues.size());
        RedmineDataConverter converter = new RedmineDataConverter(config);
        for (Issue issue : issues) {
            GTask task = converter.convertToGenericTask(issue);
            result.add(task);
        }
        return result;
    }
}
