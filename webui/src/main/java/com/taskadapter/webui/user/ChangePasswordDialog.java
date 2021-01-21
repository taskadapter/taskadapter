package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.web.InputDialog;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.taskadapter.webui.Page.message;

public class ChangePasswordDialog extends VerticalLayout {

    public interface Callback {
        /**
         * Attempts to change a password.
         *
         * @param oldPassword old password.
         * @param newPassword new password.
         * @throws AuthException          if password cannot be changed.
         * @throws WrongPasswordException if user password is wrong.
         */
        void changePassword(String oldPassword, String newPassword)
                throws AuthException, WrongPasswordException;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePasswordDialog.class);

    private final Callback callback;

    private final PasswordField oldPasswordField;
    private final PasswordField newPasswordField;
    private final PasswordField confirmPasswordField;
    private final String userName;

    private ChangePasswordDialog(String userLoginName, Callback callback) {
        this.userName = userLoginName;
        this.callback = callback;

        oldPasswordField = new PasswordField();
        oldPasswordField.setErrorMessage(message("changePassword.oldPasswordIncorrect"));

        newPasswordField = new PasswordField();

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setErrorMessage(Page.message("changePassword.newPasswordNotConfirmed"));

        FormLayout grid = new FormLayout();

        grid.add(new Label(message("changePassword.oldPassword")),
                oldPasswordField);

        grid.add(new Label(message("changePassword.newPassword")),
                newPasswordField);

        grid.add(new Label(message("changePassword.confirmPassword")),
                confirmPasswordField);

        add(grid);

        oldPasswordField.focus();
    }

    private boolean okClicked() {
        if (newPasswordMatchesConfirmation()) {
            return savePassword();
        } else {
            confirmPasswordField.setInvalid(true);
            return false;
        }
    }

    private boolean newPasswordMatchesConfirmation() {
        return newPasswordField.getValue().equals(
                confirmPasswordField.getValue());
    }

    private boolean savePassword() {
        try {
            callback.changePassword(oldPasswordField.getValue(),
                    newPasswordField.getValue());
        } catch (WrongPasswordException e) {
            oldPasswordField.setInvalid(true);
            LOGGER.error("SECURITY: wrong password provided for user " + userName + " in 'Change password' dialog.");
            return false;
        } catch (AuthException e) {
            confirmPasswordField.setInvalid(true);
            String error = "SECURITY: internal error changing password for user " + userName + " in 'Change password' dialog." + e.toString();
            LOGGER.error(error);
            Notification.show(error)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    public static void showDialog(String userLoginName, Callback callback) {
        ChangePasswordDialog dialog = new ChangePasswordDialog(userLoginName, callback);
        String caption = Page.message("changePassword.title", userLoginName);
        new InputDialog(caption, new DefaultSavableComponent(dialog, () -> dialog.okClicked())).open();
    }

}
