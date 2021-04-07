package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.data.MutableState;
import com.taskadapter.data.States;
import com.taskadapter.license.License;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.PopupDialog;
import com.taskadapter.web.event.EventCategory;
import com.taskadapter.web.event.EventTracker;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.taskadapter.webui.Page.message;

public class UsersPanel extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(UsersPanel.class);

    private final CredentialsManager credentialsManager;
    private final AuthorizedOperations authorizedOperations;
    private final License license;
    private final Button addUserButton;
    private final Text statusLabel;
    private final FormLayout usersLayout;
    private final MutableState<Integer> numUsers;
    private final Label errorLabel;

    public UsersPanel(CredentialsManager credentialsManager, AuthorizedOperations authorizedOperations, License license) {
        this.credentialsManager = credentialsManager;
        this.authorizedOperations = authorizedOperations;
        this.license = license;

        setWidth("500px");

        var captionLabel = new Html("<b>" + Page.MESSAGES.get("users.title") + "</b>");

        setMargin(true);
        setSpacing(true);
        errorLabel = new Label();
        errorLabel.setVisible(false);
        statusLabel = new Text("asdasdasdasdsad");

        usersLayout = new FormLayout();
        usersLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("40em", 1),
                new FormLayout.ResponsiveStep("30em", 2),
                new FormLayout.ResponsiveStep("20em", 3));

        var users = credentialsManager.listUsers();
        numUsers = new MutableState<Integer>(users.size());
        addUserButton = new Button(message("users.addUser"),
                event -> startCreateUserProcess());

        States.onValue(numUsers, this::applyLicenseRestriction);
        refreshUsers(users);

        add(new Hr(),
                captionLabel, errorLabel, statusLabel, usersLayout, addUserButton);

    }

    private void reloadUsers() {
        refreshUsers(credentialsManager.listUsers());
    }

    private void applyLicenseRestriction(int currentNumberOfUsersCreatedInSystem) {
        addUserButton.setEnabled(license != null
                && currentNumberOfUsersCreatedInSystem < license.getUsersNumber()
                && authorizedOperations.canAddUsers());
        if (license == null) {
            statusLabel.setText(message("users.cantAddUsersUntilLicenseInstalled"));
        } else if (license.getUsersNumber() <= currentNumberOfUsersCreatedInSystem) {
            statusLabel.setText(message("users.maximumUsersNumberReached"));
        } else {
            statusLabel.setText("");
        }
    }

    private void refreshUsers(List<String> users) {
        usersLayout.removeAll();
        users.stream().sorted().forEach(u -> addUserToPanel(u));
        numUsers.set(users.size());
    }

    private void addUserToPanel(String userLoginName) {
        var userLoginLabel = new Label(userLoginName);
        userLoginLabel.addClassName("userLoginLabelInUsersPanel");
        usersLayout.add(userLoginLabel);
        if (authorizedOperations.canChangePasswordFor(userLoginName)) {
            usersLayout.add(createSetPasswordButton(userLoginName));
        } else {
            usersLayout.add(new Label(""));
        }
        if (authorizedOperations.canDeleteUser(userLoginName)) {
            usersLayout.add(createDeleteButton(userLoginName));
        } else {
            usersLayout.add(new Label(""));
        }
    }

    private Button createSetPasswordButton(String userLoginName) {
        var setPasswordButton = new Button(message("users.setPassword"),
                event -> startSetPasswordProcess(userLoginName));
        return setPasswordButton;
    }

    private Button createDeleteButton(String userLoginName) {
        var deleteButton = new Button(message("button.delete"),
                event -> startDeleteProcess(userLoginName));
        return deleteButton;
    }

    private void startDeleteProcess(String userLoginName) {
        var deleteText = message("button.delete");
        PopupDialog.confirm(message("users.deleteUser", userLoginName),
                () -> {
                    deleteUser(userLoginName);
                });
    }

    private void deleteUser(String userLoginName) {
        try {
            credentialsManager.removeUser(userLoginName);
            EventTracker.trackEvent(EventCategory.UserCategory, "deleted", "");
        } catch (AuthException e) {
            showError(message("users.error.cantDeleteUser", e.toString()));
        }
        reloadUsers();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void startCreateUserProcess() {
        var dialog = new CreateUserDialog();
        dialog.addOKListener(() -> {
            var loginName = dialog.getLogin();
            if (!loginName.isEmpty()) {
                createUser(loginName, dialog.getPassword());
                dialog.close();
                reloadUsers();
                // TODO would be nice to show "login name is empty" warning otherwise...}
            }
        });
        dialog.open();
    }

    private void createUser(String login, String password) {
        try {
            credentialsManager.savePrimaryAuthToken(login, password);
            EventTracker.trackEvent(EventCategory.UserCategory, "created", "");
        } catch (AuthException e) {
            logger.error("User initiation error", e);
            throw new RuntimeException(e);
        }
    }

    private void startSetPasswordProcess(String userLoginName) {
        InputDialog.showSecret(message("users.changePassword", userLoginName),
                message("users.newPassword"),
                newPassword -> {
                    try {
                        credentialsManager.savePrimaryAuthToken(userLoginName, newPassword);
                        logger.info("Saved password for user " + userLoginName);
                    } catch (AuthException e) {
                        logger.error("Change password error", e);
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
