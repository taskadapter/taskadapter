package com.taskadapter.webui.user;

import com.taskadapter.web.InputDialog;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.User;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import java.util.*;

public class UsersPanel extends Panel {
    private static final int COLUMNS_NUMBER = 3;

    private Services services;
    private GridLayout usersLayout;
    private static final String DELETE_BUTTON = "Delete";
    private static final String ADMIN_LOGIN_NAME = "admin";

    public UsersPanel(Services services) {
        super("Users");
        this.services = services;
        buildUI();
    }

    private void buildUI() {
        addStyleName("panelexample");
        addCreateUserSection();
        addUsersListPanel();
        refreshUsers();
    }

    private void addUsersListPanel() {
        usersLayout = new GridLayout();
        usersLayout.setColumns(COLUMNS_NUMBER);
        usersLayout.setSpacing(true);
        addComponent(usersLayout);
    }

    private void refreshUsers() {
        usersLayout.removeAllComponents();
        List<User> users = new ArrayList<User>(services.getUserManager().getUsers());
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getLoginName().compareTo(o2.getLoginName());
            }
        });
        for (User user : users) {
            addUserToPanel(user);
        }
    }

    private void addUserToPanel(final User user) {
        Label userLoginLabel = new Label(user.getLoginName());
        userLoginLabel.addStyleName("userLoginLabelInUsersPanel");
        usersLayout.addComponent(userLoginLabel);

        Button setPasswordButton = new Button("Set password");
        setPasswordButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startSetPasswordProcess(user);
            }
        });
        usersLayout.addComponent(setPasswordButton);

        addDeleteButtonUnlessUserIsAdmin(user);
    }

    private void addDeleteButtonUnlessUserIsAdmin(final User user) {
        if (!user.getLoginName().equals(ADMIN_LOGIN_NAME)) {
            addDeleteButton(user);
        }
    }

    private void addDeleteButton(final User user) {
        Button deleteButton = new Button(DELETE_BUTTON);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startDeleteProcess(user);
            }
        });
        usersLayout.addComponent(deleteButton);
    }

    private void startDeleteProcess(final User user) {
        MessageDialog messageDialog = new MessageDialog(
                "Please confirm", "Delete user " + user.getLoginName(),
                Arrays.asList(DELETE_BUTTON, MessageDialog.CANCEL_BUTTON_LABEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (answer.equals(DELETE_BUTTON)) {
                            deleteUser(user);
                        }
                    }
                }
        );
        messageDialog.setWidth(200, UNITS_PIXELS);
        getApplication().getMainWindow().addWindow(messageDialog);
    }

    private void deleteUser(User user) {
        services.getUserManager().deleteUser(user.getLoginName());
        refreshUsers();
    }

    private void startSetPasswordProcess(final User user) {
        new InputDialog(getWindow(), "Set the new password", "New password: ",
                new InputDialog.Recipient() {
                    public void gotInput(String newPassword) {
                        setPassword(user.getLoginName(), newPassword);
                    }
                });

    }

    private void setPassword(String loginName, String newPassword) {
        services.getUserManager().setPassword(loginName, newPassword);
    }

    private void addCreateUserSection() {
        Button button = new Button("Add user");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startCreateUserProcess();
            }
        });
        addComponent(button);
    }

    private void startCreateUserProcess() {
        new InputDialog(getWindow(), "Create a new user", "Login name: ",
                new InputDialog.Recipient() {
                    public void gotInput(String loginName) {
                        createUser(loginName);
                    }
                });
    }

    private void createUser(String loginName) {
        services.getUserManager().createUser(loginName);
        refreshUsers();
    }
}
