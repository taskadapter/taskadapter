package com.taskadapter.webui.user;

import com.taskadapter.web.data.Messages;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

// TODO !! create a class like "TADialog" and move buttons and other common stuff there
public class CreateUserDialog extends Window {
    private static final Messages MESSAGES = new Messages("com.taskadapter.webui.data.messages");

    private TextField loginField;
    private PasswordField passwordField;
    private Button okButton;

    public CreateUserDialog() {
        super(MESSAGES.get("createUser.title"));

        VerticalLayout windowLayout = (VerticalLayout) this.getContent();
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);
        addComponent(layout);
        layout.setSpacing(true);
        layout.setMargin(true);

        Label loginLabel = new Label(MESSAGES.get("createUser.login"));
        layout.addComponent(loginLabel, 0, 0);
        loginField = new TextField();
        layout.addComponent(loginField, 1, 0);
        loginField.focus();
        loginField.setImmediate(true);

        Label newPassword = new Label(MESSAGES.get("createUser.password"));
        layout.addComponent(newPassword, 0, 1);
        passwordField = new PasswordField();
        layout.addComponent(passwordField, 1, 1);

        final Window dialog = this;

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        okButton = new Button(MESSAGES.get("button.create"));
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        buttonLayout.addComponent(okButton);

        Button cancelButton = new Button(MESSAGES.get("button.cancel"), new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getParent().removeWindow(dialog);
            }
        });
        buttonLayout.addComponent(cancelButton);
        windowLayout.addComponent(buttonLayout);
        windowLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
        setWidth(260, UNITS_PIXELS);
    }

    String getLogin() {
        return loginField.getValue().toString();
    }

    String getPassword() {
        return passwordField.getValue().toString();
    }

    void addOKListener(Button.ClickListener listener) {
        okButton.addListener(listener);
    }
}
