package com.taskadapter.webui.user;

import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Authenticator;
import com.taskadapter.web.service.UserManager;
import com.taskadapter.web.service.UserNotFoundException;
import com.taskadapter.web.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordDialog extends Window {
    private final Logger logger = LoggerFactory.getLogger(ChangePasswordDialog.class);
    private final Messages messages;
    private final UserManager userManager;
    private final Authenticator authenticator;

    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private GridLayout grid;
    private Label errorLabel;

    public ChangePasswordDialog(Messages messages, final UserManager userManager, final Authenticator authenticator) {
        super(messages.format("changePassword.title", authenticator.getUserName()));
        this.messages = messages;
        this.userManager = userManager;
        this.authenticator = authenticator;
        buildUI();
    }

    private void buildUI() {
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        createErrorMessageArea();
        createGrid();
        createOldPasswordField();
        createNewPasswordField();
        createConfirmField();
        createButtons();
        oldPasswordField.focus();
    }

    private void createErrorMessageArea() {
        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        addComponent(errorLabel);
    }

    private void createGrid() {
        grid = new GridLayout();
        grid.setColumns(2);
        grid.setSpacing(true);
        grid.setSpacing(true);
        grid.setMargin(true);
        addComponent(grid);
    }

    private void createButtons() {
        VerticalLayout windowLayout = (VerticalLayout) this.getContent();
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        Button okButton = new Button(messages.get("button.ok"), new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                okClicked();
            }
        });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        buttonLayout.addComponent(okButton);

        final Window dialog = this;
        Button cancelButton = new Button(messages.get("button.cancel"), new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getParent().removeWindow(dialog);
            }
        });
        buttonLayout.addComponent(cancelButton);
        windowLayout.addComponent(buttonLayout);
        windowLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
    }

    private void createConfirmField() {
        Label confirmPasswordLabel = new Label(messages.get("changePassword.confirmPassword"));
        grid.addComponent(confirmPasswordLabel);
        confirmPasswordField = new PasswordField();
        grid.addComponent(confirmPasswordField);
    }

    private void createOldPasswordField() {
        Label oldPassword = new Label(messages.get("changePassword.oldPassword"));
        grid.addComponent(oldPassword);
        oldPasswordField = new PasswordField();
        grid.addComponent(oldPasswordField);
//        oldPasswordField.setImmediate(true);
//        oldPasswordField.addListener(new FieldEvents.TextChangeListener() {
//            @Override
//            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
//                oldPasswordField.setComponentError(null);
//            }
//        });
//        oldPasswordField.addListener(new FieldEvents.FocusListener() {
//            @Override
//            public void focus(FieldEvents.FocusEvent focusEvent) {
//                oldPasswordField.setComponentError(null);
//            }
//        });
    }

    private void createNewPasswordField() {
        Label newPassword = new Label(messages.get("changePassword.newPassword"));
        grid.addComponent(newPassword);
        newPasswordField = new PasswordField();
        grid.addComponent(newPasswordField);
    }

    private void okClicked() {
        clearErrorMessage();
        if (newPasswordMatchesConfirmation()) {
            savePassword();
        } else {
            errorLabel.setValue(messages.get("changePassword.newPasswordNotConfirmed"));
        }
    }

    private void clearErrorMessage() {
        errorLabel.setValue("");
    }

    private boolean newPasswordMatchesConfirmation() {
        return newPasswordField.getValue().equals(confirmPasswordField.getValue());
    }

    private void savePassword() {
        try {
            authenticator.checkUserPassword(authenticator.getUserName(), oldPasswordField.toString());
            userManager.saveUser(authenticator.getUserName(), newPasswordField.toString());
            getParent().removeWindow(this);
        } catch (UserNotFoundException e) {
            logger.error("User not found: " + authenticator.getUserName());
        } catch (WrongPasswordException e) {
            errorLabel.setValue(messages.get("changePassword.oldPasswordIncorrect"));
            logger.error("SECURITY: wrong password provided for user " + authenticator.getUserName() + " in 'Change password' dialog.");
        }
    }
}
