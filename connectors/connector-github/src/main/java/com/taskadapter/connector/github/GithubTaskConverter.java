package com.taskadapter.connector.github;

import com.taskadapter.connector.common.TaskConverter;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubTaskConverter implements TaskConverter<Issue> {
    private final UserService userService;

    public GithubTaskConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<GTask> convertToGenericTaskList(List<Issue> issues) {
        List<GTask> tasks = new ArrayList<GTask>();
        for (Issue issue : issues) {
            GTask task = issueToGtask(issue);
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public GTask convertToGenericTask(Issue issue) {
        return issueToGtask(issue);
    }

    protected GTask issueToGtask(Issue issue) {
        GTask task = new GTask();
        task.setId(issue.getNumber());
        task.setKey(String.valueOf(issue.getNumber()));
        task.setSummary(issue.getTitle());
        task.setDescription(issue.getBody());
        task.setUpdatedOn(issue.getUpdatedAt());
        task.setCreatedOn(issue.getCreatedAt());

//        if (issue.getNumber() >= 1) {
//        	task.setRemoteId(String.valueOf(issue.getNumber()));
//        }

        if (issue != null && issue.getAssignee() != null && !"".equals(issue.getAssignee().getLogin())) {
            GUser user = new GUser(issue.getAssignee().getLogin());
            task.setAssignee(user);
        }
        return task;
    }


    private Map<String, User> ghUsers = new HashMap<String, User>();

    protected Issue gtaskToIssue(GTask task) {
        Issue issue = new Issue();
        issue.setTitle(task.getSummary());
        issue.setBody(task.getDescription());
        issue.setCreatedAt(task.getCreatedOn());
        issue.setUpdatedAt(task.getUpdatedOn());
        issue.setState(task.getDoneRatio() != null && task.getDoneRatio() == 100 ? IssueService.STATE_CLOSED : IssueService.STATE_OPEN);

        if (task.getRemoteId() != null) {
            issue.setNumber(Integer.parseInt(task.getRemoteId()));
        }

        if (task.getAssignee() != null) {
            try {
                String userLogin = task.getAssignee().getLoginName();

                if (!ghUsers.containsKey(userLogin)) {
                    User ghUser = userService.getUser(userLogin);
                    ghUser.setName(ghUser.getLogin());        // workaround for bug in eclipse-egit library - it uses name instead of login to build API request
                    ghUsers.put(userLogin, ghUser);
                }

                if (ghUsers.get(userLogin) != null) {
                    issue.setAssignee(ghUsers.get(userLogin));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return issue;
    }
}
