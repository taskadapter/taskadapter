package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.data.Messages;
import com.taskadapter.webui.service.Services;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class UsersPanel extends Panel implements LicenseChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersPanel.class);

    private static final int COLUMNS_NUMBER = 3;

    private final Messages messages;

    private Services services;
    private GridLayout usersLayout;
    private Label errorLabel;
    private Label statusLabel;
    private VerticalLayout view;
    
    private final CredentialsManager credentialsManager;

    public UsersPanel(Messages messages, Services services, CredentialsManager credentialsManager) {
        super(messages.get("users.title"));
        this.messages = messages;
        this.services = services;
        this.credentialsManager = credentialsManager;
        services.getLicenseManager().addLicenseChangeListener(this);
        view = new VerticalLayout();
        view.setMargin(true);
        setContent(view);
        refreshPage();
    }

    private void refreshPage() {
        view.removeAllComponents();
        addErrorLabel();
        addStatusLabel();
        Collection<String> users = credentialsManager.listUsers();
        addCreateUserSectionIfAllowedByLicense(users.size());
        addUsersListPanel();
        refreshUsers(users);
    }

    private void addErrorLabel() {
        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
    }

    private void addStatusLabel() {
        statusLabel = new Label();
    }

    private void addUsersListPanel() {
        usersLayout = new GridLayout();
        usersLayout.setColumns(COLUMNS_NUMBER);
        usersLayout.setSpacing(true);
        view.addComponent(usersLayout);
    }

    private void refreshUsers(final Collection<String> users) {
        usersLayout.removeAllComponents();
        List<String> usersList = new ArrayList<String>(users);
        Collections.sort(usersList);
        for (String user : usersList) {
            addUserToPanel(user);
        }
    }

    private void addUserToPanel(final String userLoginName) {
        Label userLoginLabel = new Label(userLoginName);
        userLoginLabel.addStyleName("userLoginLabelInUsersPanel");
        usersLayout.addComponent(userLoginLabel);

        final AuthorizedOperations allowedOps = services
                .getAuthorizedOperations();
        if (allowedOps.canChangePasswordFor(userLoginName)) {
            addSetPasswordButton(userLoginName);
        } else {
            usersLayout.addComponent(new Label(""));
        }

        if (allowedOps.canDeleteUser(userLoginName)) {
            addDeleteButton(userLoginName);
        } else {
            usersLayout.addComponent(new Label(""));
        }
        
    }

    private void addSetPasswordButton(final String userLoginName) {
        Button setPasswordButton = new Button(messages.get("users.setPassword"));
        setPasswordButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startSetPasswordProcess(userLoginName);
            }
        });
        usersLayout.addComponent(setPasswordButton);
    }

    private void addDeleteButton(final String userLoginName) {
        Button deleteButton = new Button(messages.get("button.delete"));
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startDeleteProcess(userLoginName);
            }
        });
        usersLayout.addComponent(deleteButton);
    }

    private void startDeleteProcess(final String userLoginName) {
        final String deleteText = messages.get("button.delete");
        MessageDialog messageDialog = new MessageDialog(
                messages.get("users.pleaseConfirm"),
                messages.format("users.deleteUser", userLoginName),
                Arrays.asList(deleteText, MessageDialog.CANCEL_BUTTON_LABEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (answer.equals(deleteText)) {
                            deleteUser(userLoginName);
                        }
                    }
                }
        );
        messageDialog.setWidth(200, PIXELS);
        getUI().addWindow(messageDialog);
    }

    private void deleteUser(String userLoginName) {
        try {
            credentialsManager.removeAuth(userLoginName);
            services.getFileManager().deleteUserFolder(userLoginName);
        } catch (AuthException e) {
            errorLabel.setValue(messages.format("users.error.cantDeleteUser", e.toString()));
        } catch (IOException e) {
            errorLabel.setValue(messages.format("users.error.cantDeleteUser", e.toString()));
        }
        refreshPage();
    }

    private void addCreateUserSectionIfAllowedByLicense(int numberOfRegisteredUsers) {
        License currentlyInstalledLicense = services.getLicenseManager().getLicense();
        if (currentlyInstalledLicense != null) {
            int maxUsersNumber = currentlyInstalledLicense.getUsersNumber();
            if (numberOfRegisteredUsers < maxUsersNumber) {
                addCreateUserSection();
            } else {
                statusLabel.setValue(messages.get("users.maximumUsersNumberReached"));
            }
        } else {
            statusLabel.setValue(messages.get("users.cantAddUsersUntilLicenseInstalled"));
        }
    }

    private void addCreateUserSection() {
        Button addUserButton = new Button(messages.get("users.addUser"));
        addUserButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startCreateUserProcess();
            }
        });
        view.addComponent(addUserButton);
    }

    private void startCreateUserProcess() {
        final CreateUserDialog dialog = new CreateUserDialog();
        dialog.addOKListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                createUser(dialog.getLogin(), dialog.getPassword());
                getUI().removeWindow(dialog);
                refreshPage();
            }
        });

        getUI().addWindow(dialog);
    }

    private void createUser(String login, String password) {
        try {
            credentialsManager.savePrimaryAuthToken(login, password);
        } catch (AuthException e) {
            LOGGER.error("User initiation error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void licenseInfoUpdated() {
        refreshPage();
    }

    private void startSetPasswordProcess(final String userLoginName) {
        InputDialog inputDialog = new InputDialog(messages.format("users.changePassword", userLoginName),
                messages.get("users.newPassword"),
                new InputDialog.Recipient() {
                    public void gotInput(String newPassword) {
                        try {
                            credentialsManager.savePrimaryAuthToken(userLoginName, newPassword);
                        } catch (AuthException e) {
                            LOGGER.error("Change password error", e);
                            throw new RuntimeException(e);
                        }
                    }
                });
        inputDialog.setPasswordMode();
        getUI().addWindow(inputDialog);
    }

}
