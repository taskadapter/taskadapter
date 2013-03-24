package com.taskadapter.connector.github;

import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.eclipse.egit.github.core.Issue;

import java.util.ArrayList;
import java.util.List;

public class GithubToGTask {

    public List<GTask> toGTaskList(List<Issue> issues) {
        List<GTask> tasks = new ArrayList<GTask>();
        for (Issue issue : issues) {
            GTask task = toGtask(issue);
            tasks.add(task);
        }
        return tasks;
    }

    protected GTask toGtask(Issue issue) {
        GTask task = new GTask();
        task.setId(issue.getNumber());
        task.setKey(String.valueOf(issue.getNumber()));
        task.setSummary(issue.getTitle());
        task.setDescription(issue.getBody());
        task.setUpdatedOn(issue.getUpdatedAt());
        task.setCreatedOn(issue.getCreatedAt());

        if (issue.getAssignee() != null && !"".equals(issue.getAssignee().getLogin())) {
            GUser user = new GUser(issue.getAssignee().getLogin());
            task.setAssignee(user);
        }
        return task;
    }

}
