package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.taskadapter.webui.Page.message;

public class ChangePasswordDialog {

    /** Change password callback. */
    public interface Callback {
        /**
         * Attempts to change a password.
         * 
         * @param oldPassword
         *            old password.
         * @param newPassword
         *            new password.
         * @throws AuthException
         *             if password cannot be changed.
         * @throws WrongPasswordException
         *             if user password is wrong.
         */
        void changePassword(String oldPassword, String newPassword)
                throws AuthException, WrongPasswordException;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePasswordDialog.class);

    private final Callback callback;

    private final PasswordField oldPasswordField;
    private final PasswordField newPasswordField;
    private final PasswordField confirmPasswordField;
    private final Label errorLabel;
    private final String userName;

//    private final Window ui;

    private ChangePasswordDialog(String name, Callback callback) {
        this.userName = name;
        this.callback = callback;
//        this.ui = new Window(message("changePassword.title", name));

        final VerticalLayout view = new VerticalLayout();
//        ui.setContent(view);
//        ui.setModal(true);
//        ui.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
//        ui.addClassName("not-maximizable-window");

        errorLabel = new Label();
        errorLabel.addClassName("errorMessage");
        view.add(errorLabel);

        /* Main layout. */
        FormLayout grid = new FormLayout();
        view.add(grid);

        grid.add(new Label(message("changePassword.oldPassword")));
        oldPasswordField = new PasswordField();
        grid.add(oldPasswordField);

        grid.add(new Label(message("changePassword.newPassword")));
        newPasswordField = new PasswordField();
        grid.add(newPasswordField);

        grid.add(new Label(message("changePassword.confirmPassword")));
        confirmPasswordField = new PasswordField();
        grid.add(confirmPasswordField);

        final Component buttonLayout = createButtons();
        view.add(buttonLayout);
//        view.setComponentAlignment(buttonLayout, FlexComponent.Alignment.BOTTOM_CENTER);

        oldPasswordField.focus();
    }

    private Component createButtons() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
//        buttonLayout.setMargin(new MarginInfo(true, false, false, false));

        Button okButton = new Button(message("button.ok"), event -> okClicked());
//        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addClassName("v-button-default");
        buttonLayout.add(okButton);

//        Window dialog = ui;
//        Button cancelButton = new Button(message("button.cancel"),
//                event -> ui.getUI().removeWindow(dialog)
//        );
//        buttonLayout.add(cancelButton);

        return buttonLayout;
    }

    private void okClicked() {
        if (newPasswordMatchesConfirmation()) {
            errorLabel.setText("");
            savePassword();
        } else {
            errorLabel.setText(message("changePassword.newPasswordNotConfirmed"));
        }
    }

    private boolean newPasswordMatchesConfirmation() {
        return newPasswordField.getValue().equals(
                confirmPasswordField.getValue());
    }

    private void savePassword() {
        try {
            callback.changePassword(oldPasswordField.getValue(),
                    newPasswordField.getValue());
//            ui.getUI().removeWindow(ui);
        } catch (WrongPasswordException e) {
            errorLabel.setText(message("changePassword.oldPasswordIncorrect"));
            LOGGER.error("SECURITY: wrong password provided for user " + userName + " in 'Change password' dialog.");
        } catch (AuthException e) {
            errorLabel.setText(message("changePassword.internalError"));
            LOGGER.error("SECURITY: internal error changing password for user " + userName + " in 'Change password' dialog." + e.toString());
        }
    }

    /**
     * Creates a window dialog.
     * 
     * @param ui
     *            base UI to show the dialog in.
     * @param name
     *            user name.
     * @param callback
     *            password callback.
     */
    public static void showDialog(UI ui, String name, Callback callback) {
//        ui.addWindow(new ChangePasswordDialog(name, callback).ui);
    }
}
