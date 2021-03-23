package com.taskadapter.webui.pages;

import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SelfManagement;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.user.ChangePasswordDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;

@Route(value = Navigator.PROFILE, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class UserProfilePage extends BasePage {

    public UserProfilePage() {
        setSpacing(true);
    }

    @Override
    protected void beforeEnter() {
        rebuildUi();
    }

    private void rebuildUi() {
        removeAll();
        addLoginInfo();
    }

    private void addLoginInfo() {
        String loginString = Page.message("userProfile.login") + ": " + SessionController.getCurrentUserName();

        Button configureSetupsButton = new Button(Page.message("userProfile.configureConnectors"),
                e -> Navigator.setupsList());
        Button button = new Button(Page.message("userProfile.changePassword"),
                e -> showChangePasswordDialog());
        Button logoutButton = new Button(Page.message("userProfile.logout"),
                e -> SessionController.logout());

        add(LayoutsUtil.centered(Sizes.mainWidth,
                new Label(loginString),
                configureSetupsButton,
                button,
                logoutButton));
    }

    /**
     * Attempts to change password for the current user.
     */
    private void showChangePasswordDialog() {
        SelfManagement selfManagement = SessionController.getUserContext().selfManagement;
        ChangePasswordDialog.showDialog(SessionController.getCurrentUserName(),
                (oldPassword, newPassword) ->
                        selfManagement.changePassword(oldPassword, newPassword));
    }
}
