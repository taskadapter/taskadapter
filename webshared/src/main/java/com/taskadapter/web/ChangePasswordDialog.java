package com.taskadapter.web;

import com.taskadapter.web.service.Authenticator;
import com.taskadapter.web.service.UserManager;
import com.taskadapter.web.service.UserNotFoundException;
import com.taskadapter.web.service.WrongPasswordException;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordDialog extends Window {
    private final Logger logger = LoggerFactory.getLogger(ChangePasswordDialog.class);

    private PasswordField oldPasswordField;
    private PasswordField newPasswordField;

    public ChangePasswordDialog(final UserManager userManager, final Authenticator authenticator) {
        super("Change password for " + authenticator.getUserName());

        VerticalLayout windowLayout = (VerticalLayout) this.getContent();
        //windowLayout.setSizeUndefined();

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);
        addComponent(layout);
        layout.setSpacing(true);
        layout.setMargin(true);

        Label oldPassword = new Label("Old password:");
        layout.addComponent(oldPassword, 0, 0);
        oldPasswordField = new PasswordField();
        layout.addComponent(oldPasswordField, 1, 0);
        oldPasswordField.focus();
        oldPasswordField.setImmediate(true);
        oldPasswordField.addListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                oldPasswordField.setComponentError(null);
            }
        });
        oldPasswordField.addListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FieldEvents.FocusEvent focusEvent) {
                oldPasswordField.setComponentError(null);
            }
        });


        Label newPassword = new Label("New password:");
        layout.addComponent(newPassword, 0, 1);
        newPasswordField = new PasswordField();
        layout.addComponent(newPasswordField, 1, 1);

        final Window dialog = this;

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        Button okButton = new Button("Ok", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                //save password
                try {
                    authenticator.checkUserPassword(authenticator.getUserName(), oldPasswordField.toString());
                    userManager.saveUser(authenticator.getUserName(), newPasswordField.toString());
                    getParent().removeWindow(dialog);
                } catch (UserNotFoundException e) {
                    logger.error("User not found: " + authenticator.getUserName());
                } catch (WrongPasswordException e) {
                    oldPasswordField.setComponentError(new UserError("Old password is incorrect."));
                    logger.error("SECURITY: wrong password for user " + authenticator.getUserName());
                }
            }
        });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        buttonLayout.addComponent(okButton);

        Button cancelButton = new Button("Cancel", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getParent().removeWindow(dialog);
            }
        });
        buttonLayout.addComponent(cancelButton);
        windowLayout.addComponent(buttonLayout);
        windowLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
    }
}
