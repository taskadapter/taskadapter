package com.taskadapter.webui.user;

import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import static com.taskadapter.webui.Page.message;

public class CreateUserDialog extends Dialog {

    private TextField loginField;
    private PasswordField passwordField;
    private Button okButton;

    public CreateUserDialog() {
        setModal(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        Html captionLabel = new Html("<b>" + Page.MESSAGES.get("createUser.title") + "</b>");

        FormLayout grid = new FormLayout();

        Label loginLabel = new Label(message("createUser.login"));
        loginField = new TextField();
        grid.add(loginLabel, loginField);

        loginField.focus();

        Label newPassword = new Label(message("createUser.password"));
        passwordField = new PasswordField();
        grid.add(newPassword, passwordField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        okButton = new Button(message("button.create"));
        okButton.addClickShortcut(Key.ENTER);
        okButton.addClassName("v-button-default");
        buttonLayout.add(okButton);

        Button cancelButton = new Button(message("button.cancel"), event ->
                close()
        );
        buttonLayout.add(cancelButton);
        setWidth("350px");

        VerticalLayout view = new VerticalLayout(captionLabel,
                grid,
                buttonLayout);
        add(view);
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
