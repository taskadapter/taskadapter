package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.data.DataCallback;
import com.taskadapter.data.MutableState;
import com.taskadapter.data.States;
import com.taskadapter.license.License;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.MessageDialog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.taskadapter.webui.Page.message;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class UsersPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersPanel.class);

    private static final int COLUMNS_NUMBER = 3;

    private final Panel ui;

    private final GridLayout usersLayout;
    private final Label errorLabel;
    private final Label statusLabel;
    private final Button addUserButton;

    private final CredentialsManager credentialsManager;
    private final AuthorizedOperations authorizedOperations;
    private final License license;
    private final MutableState<Integer> numUsers;

    /**
     * Creates a new users panel.
     * 
     * @param credentialsManager
     *            credentials manager.
     * @param authorizedOperations
     *            supported operations.
     * @param license
     *            current license.
     */
    private UsersPanel(CredentialsManager credentialsManager, AuthorizedOperations authorizedOperations, License license) {
        this.credentialsManager = credentialsManager;
        this.authorizedOperations = authorizedOperations;
        this.license = license;

        ui = new Panel(message("users.title"));

        final VerticalLayout view = new VerticalLayout();
        view.setMargin(true);
        ui.setContent(view);

        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        view.addComponent(errorLabel);

        statusLabel = new Label();
        view.addComponent(statusLabel);
        
        usersLayout = new GridLayout();
        usersLayout.setColumns(COLUMNS_NUMBER);
        usersLayout.setSpacing(true);
        view.addComponent(usersLayout);

        final Collection<String> users = credentialsManager.listUsers();
        numUsers = new MutableState<>(users.size());

        addUserButton = new Button(message("users.addUser"));
        addUserButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startCreateUserProcess();
            }
        });
        view.addComponent(addUserButton);
        States.onValue(numUsers, this::applyLicenseRestriction);
        
        refreshUsers(users);
    }
    
    private void reloadUsers() {
        refreshUsers(credentialsManager.listUsers());
    }

    private void applyLicenseRestriction(int currentNumberOfUsersCreatedInSystem) {
        addUserButton.setEnabled(license != null
                && currentNumberOfUsersCreatedInSystem < license.getUsersNumber());
        
        if (license == null) {
            statusLabel.setValue(message("users.cantAddUsersUntilLicenseInstalled"));
        } else if (license.getUsersNumber() <= currentNumberOfUsersCreatedInSystem) {
            statusLabel.setValue(message("users.maximumUsersNumberReached"));
        } else {
            statusLabel.setValue("");
        }
    }
    

    private void refreshUsers(final Collection<String> users) {
        usersLayout.removeAllComponents();
        List<String> usersList = new ArrayList<>(users);
        Collections.sort(usersList);
        for (String user : usersList) {
            addUserToPanel(user);
        }
        
        numUsers.set(usersList.size());
    }

    private void addUserToPanel(final String userLoginName) {
        Label userLoginLabel = new Label(userLoginName);
        userLoginLabel.addStyleName("userLoginLabelInUsersPanel");
        usersLayout.addComponent(userLoginLabel);

        if (authorizedOperations.canChangePasswordFor(userLoginName)) {
            usersLayout.addComponent(createSetPasswordButton(userLoginName));
        } else {
            usersLayout.addComponent(new Label(""));
        }

        if (authorizedOperations.canDeleteUser(userLoginName)) {
            usersLayout.addComponent(createDeleteButton(userLoginName));
        } else {
            usersLayout.addComponent(new Label(""));
        }

    }

    private Component createSetPasswordButton(final String userLoginName) {
        final Button setPasswordButton = new Button(message("users.setPassword"));
        setPasswordButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startSetPasswordProcess(userLoginName);
            }
        });
        return setPasswordButton;
    }

    private Component createDeleteButton(final String userLoginName) {
        final Button deleteButton = new Button(message("button.delete"));
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startDeleteProcess(userLoginName);
            }
        });
        return deleteButton;
    }

    private void startDeleteProcess(final String userLoginName) {
        final String deleteText = message("button.delete");
        MessageDialog messageDialog = new MessageDialog(
                message("users.pleaseConfirm"), message("users.deleteUser", userLoginName),
                Arrays.asList(deleteText, MessageDialog.CANCEL_BUTTON_LABEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (answer.equals(deleteText)) {
                            deleteUser(userLoginName);
                        }
                    }
                });
        messageDialog.setWidth(200, PIXELS);
        ui.getUI().addWindow(messageDialog);
    }

    private void deleteUser(String userLoginName) {
        try {
            credentialsManager.removeUser(userLoginName);
        } catch (AuthException e) {
            errorLabel.setValue(message("users.error.cantDeleteUser", e.toString()));
        }
        reloadUsers();
    }

    private void startCreateUserProcess() {
        final CreateUserDialog dialog = new CreateUserDialog();
        dialog.addOKListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                final String loginName = dialog.getLogin();
                if (!loginName.isEmpty()) {
                    createUser(loginName, dialog.getPassword());
                    ui.getUI().removeWindow(dialog);
                    reloadUsers();
                } // TODO would be nice to show "login name is empty" warning otherwise...
            }
        });

        ui.getUI().addWindow(dialog);
    }

    private void createUser(String login, String password) {
        try {
            credentialsManager.savePrimaryAuthToken(login, password);
        } catch (AuthException e) {
            LOGGER.error("User initiation error", e);
            throw new RuntimeException(e);
        }
    }

    private void startSetPasswordProcess(final String userLoginName) {
        InputDialog inputDialog = new InputDialog(message("users.changePassword", userLoginName),
                message("users.newPassword"), new InputDialog.Recipient() {
                    public void gotInput(String newPassword) {
                        try {
                            credentialsManager.savePrimaryAuthToken(
                                    userLoginName, newPassword);
                        } catch (AuthException e) {
                            LOGGER.error("Change password error", e);
                            throw new RuntimeException(e);
                        }
                    }
                });
        inputDialog.setPasswordMode();
        ui.getUI().addWindow(inputDialog);
    }

    /**
     * Renders "Users" panel.
     * 
     * @return users panel UI.
     */
    public static Component render(CredentialsManager credentialsManager,
            AuthorizedOperations supportedOperationsForCurrentUser, License license) {
        return new UsersPanel(credentialsManager, supportedOperationsForCurrentUser, license).ui;
    }
}
