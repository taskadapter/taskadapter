package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.taskadapter.vaadin14shim.GridLayout;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.vaadin.ui.UI;
import com.taskadapter.vaadin14shim.VerticalLayout;
import com.taskadapter.vaadin14shim.PasswordField;
import com.taskadapter.vaadin14shim.Button;
import com.taskadapter.vaadin14shim.Label;
import com.vaadin.ui.Window;
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

    private final Window ui;

    private ChangePasswordDialog(String name, Callback callback) {
        this.userName = name;
        this.callback = callback;
        this.ui = new Window(message("changePassword.title", name));

        final VerticalLayout view = new VerticalLayout();
        ui.setContent(view);
        ui.setModal(true);
        ui.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        ui.addStyleName("not-maximizable-window");

        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        view.add(errorLabel);

        /* Main layout. */
        final GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setSpacing(true);
        grid.setSpacing(true);
        grid.setMargin(true);
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
        view.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);

        oldPasswordField.focus();
    }

    private Component createButtons() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(new MarginInfo(true, false, false, false));

        Button okButton = new Button(message("button.ok"), event -> okClicked());
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addClassName("v-button-default");
        buttonLayout.add(okButton);

        Window dialog = ui;
        Button cancelButton = new Button(message("button.cancel"),
                event -> ui.getUI().removeWindow(dialog)
        );
        buttonLayout.add(cancelButton);

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
            ui.getUI().removeWindow(ui);
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
        ui.addWindow(new ChangePasswordDialog(name, callback).ui);
    }
}
