package com.taskadapter.connector.basecamp.classic.editor;

import com.taskadapter.connector.basecamp.classic.BasecampConfig;
import com.taskadapter.connector.basecamp.classic.BasecampUtils;
import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.web.ExceptionFormatter;
import com.vaadin.ui.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public final class ShowInfoElement {
    static void loadProject(BasecampConfig config, ExceptionFormatter exceptionFormatter, ObjectAPIFactory factory) {
        try {
            BasecampProject project = BasecampUtils.loadProject(factory, config);
            showProjectInfo(project);

        } catch (BadConfigException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            Notification.show("Oops", localizedMessage, Notification.Type.ERROR_MESSAGE);
        }
    }

    private static void showProjectInfo(BasecampProject project) {
        String msg = "<BR>Key:  " + project.getKey()
                + "<BR>Name: " + project.getName();
        Notification.show("Project Info", msg, Notification.Type.HUMANIZED_MESSAGE);
    }

    public static void showTodoListInfo(BasecampConfig config, ExceptionFormatter formatter, ObjectAPIFactory factory) {
        try {
            TodoList todoList = BasecampUtils.loadTodoList(factory, config);
            showTodoListInfoPopup(todoList);

        } catch (BadConfigException e) {
            String localizedMessage = formatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = formatter.formatError(e);
            Notification.show("Oops", localizedMessage, Notification.Type.ERROR_MESSAGE);
        }
    }

    private static void showTodoListInfoPopup(TodoList todoList) {
        String msg = "<BR>Key:  " + todoList.getKey()
                + "<BR>Name: " + todoList.getName()
                + "<BR>Description: " + nvl(todoList.getDescription())
                + "<BR>Completed Todos: " + todoList.getCompletedCount()
                + "<BR>Remaining Todos: " + todoList.getRemainingCount();

        Notification.show("Todo List Info", msg, Notification.Type.HUMANIZED_MESSAGE);
    }
}
