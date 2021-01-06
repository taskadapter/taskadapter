package com.taskadapter.webui.user;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.taskadapter.vaadin14shim.GridLayout;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.taskadapter.vaadin14shim.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.taskadapter.vaadin14shim.VerticalLayout;
import com.vaadin.ui.Window;

import static com.taskadapter.webui.Page.message;
import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class CreateUserDialog extends Window {

    private TextField loginField;
    private PasswordField passwordField;
    private Button okButton;

    public CreateUserDialog() {
        super(message("createUser.title"));

        VerticalLayout view = new VerticalLayout();
        setContent(view);
        setModal(true);
        addStyleName("not-maximizable-window");

        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        GridLayout grid = new GridLayout(2, 2);
        view.add(grid);
        grid.setSpacing(true);
        grid.setSpacing(true);
        grid.setMargin(true);

        Label loginLabel = new Label(message("createUser.login"));
        grid.add(loginLabel, 0, 0);
        loginField = new TextField();
        grid.add(loginField, 1, 0);
        loginField.focus();
        loginField.setImmediate(true);

        Label newPassword = new Label(message("createUser.password"));
        grid.add(newPassword, 0, 1);
        passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        final Window dialog = this;

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(new MarginInfo(true, false, false, false));

        okButton = new Button(message("button.create"));
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addClassName("v-button-default");
        buttonLayout.add(okButton);

        Button cancelButton = new Button(message("button.cancel"),
                event -> getUI().removeWindow(dialog));
        buttonLayout.add(cancelButton);
        view.add(buttonLayout);
        view.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
        setWidth(350, PIXELS);
    }

    public String getLogin() {
        return loginField.getValue();
    }

    public String getPassword() {
        return passwordField.getValue();
    }

    void addOKListener(Runnable okListener) {
        okButton.addClickListener(event -> okListener.run());
    }
}
