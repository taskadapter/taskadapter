package com.taskadapter.connector.jira;

public class JiraTaskTypeResolver {
    public static String resolveIssueTypeNameForCreate(IssueWrapper wrapper,
                                                       String defaultTaskTypeName,
                                                       String defaultIssueTypeForSubtasks) {
        if (wrapper.getTaskType().isPresent() && !wrapper.getTaskType().get().isEmpty()) {
            return wrapper.getTaskType().get();
        } else {
            if (wrapper.getIssueInput().getField("parent") == null) {
                return defaultTaskTypeName;
            }
            return defaultIssueTypeForSubtasks;
        }
    }
}
