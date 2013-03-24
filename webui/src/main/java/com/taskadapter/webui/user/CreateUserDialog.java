package com.taskadapter.webui.user;

import com.taskadapter.web.data.Messages;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

// TODO !! create a class like "TADialog" and move buttons and other common stuff there
public class CreateUserDialog extends Window {
    private static final Messages MESSAGES = new Messages("com.taskadapter.webui.data.messages");

    private TextField loginField;
    private PasswordField passwordField;
    private Button okButton;

    public CreateUserDialog() {
        super(MESSAGES.get("createUser.title"));

        VerticalLayout view = new VerticalLayout();
        setContent(view);
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        GridLayout grid = new GridLayout(2, 2);
        view.addComponent(grid);
        grid.setSpacing(true);
        grid.setSpacing(true);
        grid.setMargin(true);

        Label loginLabel = new Label(MESSAGES.get("createUser.login"));
        grid.addComponent(loginLabel, 0, 0);
        loginField = new TextField();
        grid.addComponent(loginField, 1, 0);
        loginField.focus();
        loginField.setImmediate(true);

        Label newPassword = new Label(MESSAGES.get("createUser.password"));
        grid.addComponent(newPassword, 0, 1);
        passwordField = new PasswordField();
        grid.addComponent(passwordField, 1, 1);

        final Window dialog = this;

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(new MarginInfo(true, false, false, false));

        okButton = new Button(MESSAGES.get("button.create"));
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        buttonLayout.addComponent(okButton);

        Button cancelButton = new Button(MESSAGES.get("button.cancel"), new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getUI().removeWindow(dialog);
            }
        });
        buttonLayout.addComponent(cancelButton);
        view.addComponent(buttonLayout);
        view.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
        setWidth(260, PIXELS);
    }

    String getLogin() {
        return loginField.getValue();
    }

    String getPassword() {
        return passwordField.getValue();
    }

    void addOKListener(Button.ClickListener listener) {
        okButton.addClickListener(listener);
    }
}
