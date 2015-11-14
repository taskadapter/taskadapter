package com.taskadapter.webui.user;

import com.taskadapter.auth.AuthException;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
        view.addComponent(errorLabel);

        /* Main layout. */
        final GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setSpacing(true);
        grid.setSpacing(true);
        grid.setMargin(true);
        view.addComponent(grid);

        grid.addComponent(new Label(message("changePassword.oldPassword")));
        oldPasswordField = new PasswordField();
        grid.addComponent(oldPasswordField);

        grid.addComponent(new Label(message("changePassword.newPassword")));
        newPasswordField = new PasswordField();
        grid.addComponent(newPasswordField);

        grid.addComponent(new Label(message("changePassword.confirmPassword")));
        confirmPasswordField = new PasswordField();
        grid.addComponent(confirmPasswordField);

        final Component buttonLayout = createButtons();
        view.addComponent(buttonLayout);
        view.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);

        oldPasswordField.focus();
    }

    private Component createButtons() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(new MarginInfo(true, false, false, false));

        Button okButton = new Button(message("button.ok"),
                new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        okClicked();
                    }
                });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        buttonLayout.addComponent(okButton);

        final Window dialog = ui;
        Button cancelButton = new Button(message("button.cancel"),
                new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        ui.getUI().removeWindow(dialog);
                    }
                });
        buttonLayout.addComponent(cancelButton);

        return buttonLayout;
    }

    private void okClicked() {
        if (newPasswordMatchesConfirmation()) {
            errorLabel.setValue("");
            savePassword();
        } else {
            errorLabel.setValue(message("changePassword.newPasswordNotConfirmed"));
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
            errorLabel.setValue(message("changePassword.oldPasswordIncorrect"));
            LOGGER.error("SECURITY: wrong password provided for user " + userName + " in 'Change password' dialog.");
        } catch (AuthException e) {
            errorLabel.setValue(message("changePassword.internalError"));
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
