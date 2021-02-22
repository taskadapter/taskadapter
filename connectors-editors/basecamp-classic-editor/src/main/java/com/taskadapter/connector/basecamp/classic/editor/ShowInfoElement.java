package com.taskadapter.connector.basecamp.classic.editor;

import com.taskadapter.connector.basecamp.classic.BasecampClassicConfig;
import com.taskadapter.connector.basecamp.classic.BasecampUtils;
import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.webui.NotificationUtil;
import com.vaadin.flow.component.notification.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public final class ShowInfoElement {
    // TODO 14 delete
    static void loadProject(BasecampClassicConfig config, WebConnectorSetup setup, ExceptionFormatter exceptionFormatter, ObjectAPIFactory factory) {
        try {
            BasecampProject project = BasecampUtils.loadProject(factory, config, setup);
            showProjectInfo(project);
        } catch (ConnectorException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            NotificationUtil.showError(localizedMessage);
        }
    }

    private static void showProjectInfo(BasecampProject project) {
        String msg = "<BR>Key:  " + project.getKey()
                + "<BR>Name: " + project.getName();
        Notification.show(msg);
    }

    public static void showTodoListInfo(BasecampClassicConfig config, WebConnectorSetup setup, ExceptionFormatter formatter, ObjectAPIFactory factory) {
        try {
            TodoList todoList = BasecampUtils.loadTodoList(factory, config, setup);
            showTodoListInfoPopup(todoList);

        } catch (BadConfigException e) {
            String localizedMessage = formatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = formatter.formatError(e);
            NotificationUtil.showError(localizedMessage);
        }
    }

    private static void showTodoListInfoPopup(TodoList todoList) {
        String msg = "<BR>Key:  " + todoList.getKey()
                + "<BR>Name: " + todoList.getName()
                + "<BR>Description: " + nvl(todoList.getDescription())
                + "<BR>Completed Todos: " + todoList.getCompletedCount()
                + "<BR>Remaining Todos: " + todoList.getRemainingCount();

        Notification.show(msg);
    }
}
