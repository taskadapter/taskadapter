package com.taskadapter.webui.pages;

import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.WebUserSession;
import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = Navigator.LOGIN, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class LoginPage extends BasePage {
    private static final int FORM_WIDTH = 300;
    private static final String EDIT_WIDTH = "260px";

    public LoginPage() {
        buildUi();
    }

    public void buildUi() {
        H1 captionLabel = new H1(Page.message("loginPage.login"));

        VerticalLayout layout = new VerticalLayout();
        layout.add(captionLabel);

        layout.setSpacing(true);
        Html label = new Html(Page.message("loginPage.hintLabel"));
        layout.add(label);

        TextField loginField = new TextField(Page.message("loginPage.login"));
        loginField.setWidth(EDIT_WIDTH);
        layout.add(loginField);

        PasswordField passwordField = new PasswordField(Page.message("loginPage.password"));
        passwordField.setWidth(EDIT_WIDTH);
        layout.add(passwordField);

        loginField.setValue("admin");
        passwordField.setValue("admin");

        Checkbox staySignedIn = new Checkbox(Page.message("loginPage.staySignedIn"));
        layout.add(staySignedIn);

        Label errorLabel = new Label("");
        errorLabel.addClassName("errorMessage");
        layout.add(errorLabel);

        Button loginButton = new Button(Page.message("loginPage.loginButton"),
                event -> {
                    String username = loginField.getValue();
                    String password = passwordField.getValue();
                    try {
                        authenticate(username, password,
                                staySignedIn.getValue());
                        SessionController.initSession(new WebUserSession().setCurrentUserName(username));
                        redirectToNextPage();
                    } catch (WrongPasswordException e) {
                        errorLabel.setText(Page.message("loginPage.wrongUserNameOrPassword"));
                        passwordField.setValue("");
                        passwordField.focus();
                    }
                });
        loginButton.addClickShortcut(Key.ENTER);

        layout.add(loginButton);
        loginField.focus();

        add(LayoutsUtil.centeredLayout(layout, FORM_WIDTH));
    }

    private void redirectToNextPage() {
        Navigator.configsList();
    }

    /**
     * Performs user authentication.
     *
     * @param user                user name.
     * @param password            password.
     * @param enableSecondaryAuth flag, indicating, that secondary device authentication
     *                            (non-password) should be enabled.
     * @throws WrongPasswordException if username or password is wrong.
     */
    private void authenticate(String user, String password,
                              boolean enableSecondaryAuth) throws WrongPasswordException {
        SessionController.tryAuth(user, password, enableSecondaryAuth);
    }
}
