package com.taskadapter.webui.user;

import com.taskadapter.config.User;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UserManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import java.io.IOException;
import java.util.*;

public class UsersPanel extends Panel implements LicenseChangeListener {
    private static final int COLUMNS_NUMBER = 3;

    private Services services;
    private GridLayout usersLayout;
    private static final String DELETE_BUTTON = "Delete";
    private Label errorLabel;

    public UsersPanel(Services services) {
        super("Users");
        this.services = services;
        addStyleName("panelexample");
        services.getLicenseManager().addLicenseChangeListener(this);
        refreshPage();
    }

    private void refreshPage() {
        removeAllComponents();
        addErrorLabel();
        Collection<User> users = services.getUserManager().getUsers();
        addCreateUserSectionIfAllowedByLicense(users.size());
        addUsersListPanel();
        refreshUsers(users);
    }

    private void addErrorLabel() {
        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
    }

    private void addUsersListPanel() {
        usersLayout = new GridLayout();
        usersLayout.setColumns(COLUMNS_NUMBER);
        usersLayout.setSpacing(true);
        addComponent(usersLayout);
    }

    private void refreshUsers(final Collection<User> users) {
        usersLayout.removeAllComponents();
        List<User> usersList = new ArrayList<User>(users);
        Collections.sort(usersList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getLoginName().compareTo(o2.getLoginName());
            }
        });
        for (User user : usersList) {
            addUserToPanel(user.getLoginName());
        }
    }

    private void addUserToPanel(final String userLoginName) {
        Label userLoginLabel = new Label(userLoginName);
        userLoginLabel.addStyleName("userLoginLabelInUsersPanel");
        usersLayout.addComponent(userLoginLabel);

        if (services.getUserManager().isAdmin(services.getAuthenticator().getUserName())) {
            addSetPasswordButton(userLoginName);
            addDeleteButtonUnlessUserIsHardcodedAdminUser(userLoginName);
        } else {
            // fillers for gridlayout
            usersLayout.addComponent(new Label(""));
            usersLayout.addComponent(new Label(""));
        }

    }

    private void addSetPasswordButton(final String userLoginName) {
        Button setPasswordButton = new Button("Set password");
        setPasswordButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                EditorUtil.startSetPasswordProcess(getWindow(), services.getUserManager(), userLoginName);
            }
        });
        usersLayout.addComponent(setPasswordButton);
    }

    private void addDeleteButtonUnlessUserIsHardcodedAdminUser(final String userLoginName) {
        if (!userLoginName.equals(UserManager.ADMIN_LOGIN_NAME)) {
            addDeleteButton(userLoginName);
        } else {
            // filler for gridlayout
            usersLayout.addComponent(new Label(""));
        }
    }

    private void addDeleteButton(final String userLoginName) {
        Button deleteButton = new Button(DELETE_BUTTON);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startDeleteProcess(userLoginName);
            }
        });
        usersLayout.addComponent(deleteButton);
    }

    private void startDeleteProcess(final String userLoginName) {
        MessageDialog messageDialog = new MessageDialog(
                "Please confirm", "Delete user " + userLoginName,
                Arrays.asList(DELETE_BUTTON, MessageDialog.CANCEL_BUTTON_LABEL),
                new MessageDialog.Callback() {
                    public void onDialogResult(String answer) {
                        if (answer.equals(DELETE_BUTTON)) {
                            deleteUser(userLoginName);
                        }
                    }
                }
        );
        messageDialog.setWidth(200, UNITS_PIXELS);
        getApplication().getMainWindow().addWindow(messageDialog);
    }

    private void deleteUser(String userLoginName) {
        try {
            services.getUserManager().deleteUser(userLoginName);
        } catch (IOException e) {
            errorLabel.setValue("Can't delete user. " + e.getMessage());
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
                addComponent(new Label("Maximum users number allowed by your license is reached."));
            }
        } else {
            addComponent(new Label("Can't add users until a license is installed."));
        }
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
        InputDialog inputDialog = new InputDialog("Create a new user", "Login name: ",
                new InputDialog.Recipient() {
                    public void gotInput(String loginName) {
                        createUser(loginName);
                    }
                });
        getWindow().addWindow(inputDialog);
    }

    private void createUser(String loginName) {
        services.getUserManager().saveUser(loginName, "");
        refreshPage();
    }

    @Override
    public void licenseInfoUpdated() {
        refreshPage();
    }
}
