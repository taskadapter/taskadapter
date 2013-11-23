package com.taskadapter.webui.pages;

import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

/**
 * System login page. Does not perform authentication by itself, delegates
 * handling to the callback instance.
 */
public final class LoginPage {

    /**
     * Callbacks for login operation.
     */
    public static interface Callback {
        /**
         * Performs user authentication and all other required operations.
         * 
         * @param user
         *            user name.
         * @param password
         *            password.
         * @param enableSecondaryAuth
         *            flag, indicating, that secondary device authentication
         *            (non-password) should be enabled.
         * @throws WrongPasswordException
         *             if username or password is wrong.
         */
        void authenticate(String user, String password,
                boolean enableSecondaryAuth) throws WrongPasswordException;
    }

    /**
     * Creates a page ui.
     * 
     * @param callback
     *            page callback.
     */
    public static Component createUI(final Callback callback) {
        final Panel panel = new Panel(LOG_IN_TITLE);

        final VerticalLayout layout = new VerticalLayout();
        panel.setContent(layout);
        layout.setWidth(FORM_WIDTH);

        layout.setMargin(new MarginInfo(false, true, false, true));
        layout.setSpacing(true);
        final Label label = new Label(HINT_LABEL, ContentMode.HTML);
        label.setStyleName(Runo.LABEL_SMALL);
        layout.addComponent(label);

        final TextField loginEdit = new TextField();
        loginEdit.setCaption(LOGIN_PROMPT);
        loginEdit.setWidth(EDIT_WIDTH);
        layout.addComponent(loginEdit);

        final PasswordField passwordEdit = new PasswordField();
        passwordEdit.setCaption(PASSWORD_PROMPT);
        passwordEdit.setWidth(EDIT_WIDTH);
        layout.addComponent(passwordEdit);

        final CheckBox staySignedIn = new CheckBox(STAY_SIGNED_IN);
        layout.addComponent(staySignedIn);

        final Button loginButton = new Button(LOG_IN_TITLE);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(Runo.BUTTON_DEFAULT);
        loginButton.addStyleName(Runo.BUTTON_BIG);

        final Label errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        layout.addComponent(errorLabel);

        loginButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String username = loginEdit.getValue();
                String password = passwordEdit.getValue();
                try {
                    callback.authenticate(username, password,
                            staySignedIn.getValue());
                } catch (WrongPasswordException e) {
                    errorLabel.setValue("Wrong user name or password.");
                    passwordEdit.setValue("");
                    passwordEdit.focus();
                }
            }
        });
        layout.addComponent(loginButton);
        loginEdit.focus();

        return panel;
    }

    private static final String LOG_IN_TITLE = "Log in";
    private static final String LOGIN_PROMPT = "Login";
    private static final String PASSWORD_PROMPT = "Password";
    private static final String STAY_SIGNED_IN = "Stay signed in";
    private static final String HINT_LABEL = "<i>Default login/password: admin/admin</i>";
    private static final String FORM_WIDTH = "300px";
    private static final String EDIT_WIDTH = "260px";

}
