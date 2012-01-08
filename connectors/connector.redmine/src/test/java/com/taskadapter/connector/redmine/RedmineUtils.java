package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RedmineUtils {
    public static List<GTask> createIssues(RedmineConfig redmineConfig, String projectKey, int issuesNumber) {
        List<GTask> issues = new ArrayList<GTask>(issuesNumber);
        RedmineManager mgr = new RedmineManager(RedmineTestConfig.getURI(),
                RedmineTestConfig.getUserLogin(), RedmineTestConfig.getPassword());

        List<Issue> issuesToCreate = generateIssues(issuesNumber);

        RedmineDataConverter converter = new RedmineDataConverter(redmineConfig);
        for (int i = 0; i < issuesToCreate.size(); i++) {
            Issue issue;
            try {
                issue = mgr.createIssue(projectKey, issuesToCreate.get(i));
            } catch (Exception e) {
                throw new RuntimeException(e.toString(), e);
            }
            GTask task = converter.convertToGenericTask(issue);
            issues.add(task);
        }

        return issues;

    }


    private static List<Issue> generateIssues(int issuesNumber) {
		List<Issue> issues = new ArrayList<Issue>(issuesNumber);
		Random r = new Random();
		for (int i = 0; i < issuesNumber; i++) {
			Issue issue = new Issue();
			issue.setSubject("some issue " + i + " " + new Date());
			issue.setEstimatedHours((float) r.nextInt(40));
			issues.add(issue);
		}
		return issues;
	}

    public static GTask generateTaskWithPrecedesRelations(RedmineConnector redmine,
                                                          Integer childCount) throws Exception {
        List<GTask> list = new ArrayList<GTask>();

        GTask task = TestUtils.generateTask();
        task.setId(1);
        list.add(task);

        for (int i = 0; i < childCount; i++) {
            GTask task1 = TestUtils.generateTask();
            task1.setId(i + 2);

            task1.getRelations().add(new GRelation(task1.getId().toString(), task.getId().toString(), GRelation.TYPE.precedes));
            list.add(task1);
        }

        List<GTask> loadedList = TestUtils.saveAndLoadList(redmine, list);

        GTask t = TestUtils.findTaskBySummary(loadedList, task.getSummary());

        return t;
    }
}
