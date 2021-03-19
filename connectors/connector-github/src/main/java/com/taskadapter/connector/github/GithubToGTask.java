package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;

import java.util.List;
import java.util.stream.Collectors;

public class GithubToGTask {
    public static List<GTask> toGTaskList(List<Issue> issues) {
        return issues.stream()
                .map(GithubToGTask::toGtask)
                .collect(Collectors.toList());
    }

    public static GTask toGtask(Issue issue) {
        var task = new GTask();
        var stringKey = Integer.toString(issue.getNumber());
        task.setId(Long.parseLong(stringKey));
        task.setKey(stringKey);
        task.setSourceSystemId(new TaskId(issue.getId(), stringKey));
        task.setValue(AllFields.summary, issue.getTitle());
        task.setValue(AllFields.description, issue.getBody());
        task.setValue(AllFields.createdOn, issue.getCreatedAt());
        task.setValue(AllFields.updatedOn, issue.getUpdatedAt());
        if (issue.getAssignee() != null) {
            task.setValue(AllFields.assigneeFullName, issue.getAssignee().getName());
            task.setValue(AllFields.assigneeLoginName, issue.getAssignee().getLogin());
        }
        return task;
    }
}
