package com.taskadapter.webui.user;

import com.taskadapter.config.User;
import com.taskadapter.license.License;
import com.taskadapter.license.LicenseChangeListener;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UserManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersPanel extends Panel implements LicenseChangeListener {

    private static final int COLUMNS_NUMBER = 3;

    private final Messages messages;

    private Services services;
    private GridLayout usersLayout;
    private Label errorLabel;
    private Button addUserButton;
    private Label statusLabel;

    public UsersPanel(Messages messages, Services services) {
        super(messages.get("users.title"));
        this.messages = messages;
        this.services = services;
        services.getLicenseManager().addLicenseChangeListener(this);
        refreshPage();
    }

    private void refreshPage() {
        removeAllComponents();
        addErrorLabel();
        addStatusLabel();
        Collection<User> users = services.getUserManager().getUsers();
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

        if (services.getUserManager().isAdmin(services.getCurrentUserInfo().getUserName())) {
            addSetPasswordButton(userLoginName);
            addDeleteButtonUnlessUserIsHardcodedAdminUser(userLoginName);
        } else {
            // fillers for gridlayout
            usersLayout.addComponent(new Label(""));
            usersLayout.addComponent(new Label(""));
        }

    }

    private void addSetPasswordButton(final String userLoginName) {
        Button setPasswordButton = new Button(messages.get("users.setPassword"));
        setPasswordButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startSetPasswordProcess(getWindow(), services.getUserManager(), userLoginName);
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
        Button deleteButton = new Button(messages.get("button.delete"));
        deleteButton.addListener(new Button.ClickListener() {
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
        messageDialog.setWidth(200, UNITS_PIXELS);
        getApplication().getMainWindow().addWindow(messageDialog);
    }

    private void deleteUser(String userLoginName) {
        try {
            services.getUserManager().deleteUser(userLoginName);
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
        addUserButton = new Button(messages.get("users.addUser"));
        addUserButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                startCreateUserProcess();
            }
        });
        addComponent(addUserButton);
    }

    private void startCreateUserProcess() {
        final CreateUserDialog dialog = new CreateUserDialog();
        dialog.addOKListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                createUser(dialog.getLogin(), dialog.getPassword());
                getWindow().removeWindow(dialog);
                refreshPage();
            }
        });

        getWindow().addWindow(dialog);
    }

    private void createUser(String login, String password) {
        services.getUserManager().saveUser(login, password);
    }

    @Override
    public void licenseInfoUpdated() {
        refreshPage();
    }

    String getStatusLabelText() {
        return (String) statusLabel.getValue();
    }

    Button getAddUserButton() {
        return addUserButton;
    }

    // TODO this is similar to startChangePasswordProcess() in Header class.
    private void startSetPasswordProcess(Window parentWindow, final UserManager userManager, final String userLoginName) {
        InputDialog inputDialog = new InputDialog(messages.format("users.changePassword", userLoginName),
                messages.get("users.newPassword"),
                new InputDialog.Recipient() {
                    public void gotInput(String newPassword) {
                        userManager.saveUser(userLoginName, newPassword);
                    }
                });
        inputDialog.setPasswordMode();
        parentWindow.addWindow(inputDialog);
    }

}
