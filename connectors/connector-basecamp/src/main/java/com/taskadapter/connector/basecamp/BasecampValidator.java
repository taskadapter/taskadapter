package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;

import java.util.ArrayList;
import java.util.List;

public class BasecampValidator {
    static void validateConfigWithException(BasecampConfig config) throws BadConfigException {
        failIfErrors(validateConfig(config));
    }

    public static void validateAccountWithException(BasecampConfig config) throws BadConfigException {
        failIfErrors(validateAccount(config));
    }

    static void validateProjectWithException(BasecampConfig config) throws BadConfigException {
        failIfErrors(validateProject(config));
    }

    private static void failIfErrors(List<BadConfigException> errors) throws BadConfigException {
        if (!errors.isEmpty()) {
            throw errors.get(0);
        }
    }

    public static List<BadConfigException> validateConfig(BasecampConfig config) {
        var list = new ArrayList<BadConfigException>();
        list.addAll(validateAccount(config));
        list.addAll(validateProject(config));
        list.addAll(validateTodolist(config));
        return list;
    }

    static List<BadConfigException> validateAccount(BasecampConfig config) {
        var list = new ArrayList<BadConfigException>();
        var accountId = config.getAccountId();
        if (accountId == null || accountId.isEmpty()) {
            list.add(new FieldNotSetException("account-id"));
        }
        if (!isNum(accountId)) {
            list.add(new BadFieldException("account-id"));
        }
        return list;
    }

    static List<BadConfigException> validateProject(BasecampConfig config) {
        var list = new ArrayList<BadConfigException>();
        var projectKey = config.getProjectKey();
        if (projectKey == null) {
            list.add(new FieldNotSetException("project-key"));
        }
        if (!isNum(projectKey)) {
            list.add(new BadFieldException("project-key"));
        }
        return list;
    }

    static List<BadConfigException> validateTodolist(BasecampConfig config) {
        var list = new ArrayList<BadConfigException>();
        var listKey = config.getTodoKey();
        if (listKey == null) {
            list.add(new FieldNotSetException("todo-key"));
        }
        if (!isNum(listKey)) {
            list.add(new BadFieldException("todo-key"));
        }
        return list;
    }

    private static boolean isNum(String str) {
        return str.chars().allMatch(Character::isDigit);
    }
}

