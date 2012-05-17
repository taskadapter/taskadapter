package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.web.MessageDialog;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UpdateManager;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public class Navigator {
    public static final String CONFIGS = "configs_list";
    public static final String HOME = "home";
    public static final String CONFIGURE_SYSTEM_PAGE = "configure_page";
    public static final String FEEDBACK_PAGE = "feedback";
    public static final String NEW_CONFIG_PAGE = "new_config_page";
    public static final String CONFIGURE_TASK_PAGE = "configure_task";

    private static final String LOGIN_PAGE = "login_page";
    private static final String DELETE_PAGE = "delete_task";
    public static final String MAIN_WIDTH = "920px";// like GitHub

//    private static final String CANT_USE_TA_FROM_REMOTE_MACHINE_IN_SINGLE_USER_LICENSE_PAGE = "no_access_from_remote_in_single_user_mode_page";

    private Map<String, Page> pages = new HashMap<String, Page>();

    private HorizontalLayout navigationPanel;
    private HorizontalLayout currentComponentArea = new HorizontalLayout();
    private Layout mainArea = new CssLayout();
    private VerticalLayout layout;
    private Services services;
    private Label updateMessage;
    Header header;
    private Page previousPage;
    private Page currentPage;

    public Navigator(VerticalLayout layout, Services services) {
        this.layout = layout;
        this.services = services;
        registerPages();
        buildUI();
        checkLastAvailableVersion();
    }

    private void registerPages() {
        ConfigsPage configsPage = new ConfigsPage();
        registerPage(CONFIGS, configsPage);
        registerPage(LOGIN_PAGE, new LoginPage());
        registerPage(HOME, configsPage);
        registerPage(CONFIGURE_SYSTEM_PAGE, new ConfigureSystemPage());
        registerPage(FEEDBACK_PAGE, new SupportPage());
        registerPage(NEW_CONFIG_PAGE, new NewConfigPage());
        registerPage(CONFIGURE_TASK_PAGE, new ConfigureTaskPage());
        registerPage(DELETE_PAGE, new ConfirmationPage());
    }

    private void buildUI() {
        header = new Header(this, services);
        header.setHeight(50, Sizeable.UNITS_PIXELS);
        header.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        layout.addComponent(header);

        addUpdatePanel();
        addNavigationPanel();

        // the big shadowed page on middle
        mainArea.setStyleName(Runo.CSSLAYOUT_SHADOW);
        mainArea.setWidth(MAIN_WIDTH);

        // container for currentComponentArea to be aligned in mainArea correctly
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(currentComponentArea);

        mainArea.addComponent(verticalLayout);
        verticalLayout.setComponentAlignment(currentComponentArea, Alignment.MIDDLE_CENTER);

        layout.addComponent(mainArea);
        layout.setComponentAlignment(mainArea, Alignment.MIDDLE_CENTER);
    }

    private void addUpdatePanel() {
        updateMessage = new Label();
        layout.addComponent(updateMessage);
        layout.setComponentAlignment(updateMessage, Alignment.MIDDLE_CENTER);
    }

    private void addNavigationPanel() {
        navigationPanel = new HorizontalLayout();
        navigationPanel.setHeight("30px");
        navigationPanel.setSpacing(true);
        layout.addComponent(navigationPanel);
        layout.setComponentAlignment(navigationPanel, Alignment.MIDDLE_CENTER);
    }

    public void registerPage(String id, Page page) {
        pages.put(id, page);
    }

    public void show(String pageId) {
        Page page = pages.get(pageId);
        if (page != null) {
            show(page);
        } else {
            showError("Internal error!", "Page \"" + pageId + "\" is not registered");
        }
    }

    public void show(final Page page) {
        previousPage = currentPage;

        currentPage = page;
        if (!services.getAuthenticator().isLoggedIn()) {
            currentPage = pages.get(LOGIN_PAGE);
        }


        boolean singleUserMode = services.getSettingsManager().isTAWorkingOnLocalMachine();
        boolean requestCameFromLocalhost = services.getSessionInfo().isRequestCameFromLocalhost();
        if (singleUserMode && !requestCameFromLocalhost) {
            currentPage = pages.get(CONFIGURE_SYSTEM_PAGE);
            ((ConfigureSystemPage) currentPage).setError("Access from remote machine is forbidden in LOCAL (single user) mode.<BR>Please change to Server mode if you have a Server License.");
        } else {
            ConfigureSystemPage tmpPage = (ConfigureSystemPage) pages.get(CONFIGURE_SYSTEM_PAGE);
            tmpPage.clearError();
        }
        setServicesToPage(currentPage);

        currentComponentArea.removeAllComponents();
        Component ui = currentPage.getUI();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT);

        navigationPanel.removeAllComponents();

        Label titleLabel = new Label(currentPage.getPageTitle());
        titleLabel.setSizeUndefined();
        navigationPanel.addComponent(titleLabel);
    }

    private void setServicesToPage(Page page) {
        page.setNavigator(this);
        page.setServices(services);
    }

    // TODO these 5 showXX methods are not in line with the other show(). refactor!
    public void showConfigureTaskPage(TAFile file) {
        showConfigureTaskPage(file, null, null);
    }

    public void showConfigureTaskPage(TAFile file, String configLabelEqualsTabName) {
        showConfigureTaskPage(file, configLabelEqualsTabName, null);
    }

    public void showConfigureTaskPage(TAFile file, String dataHolderLabel, String errorMessage) {
        ConfigureTaskPage page = (ConfigureTaskPage) pages.get(CONFIGURE_TASK_PAGE);
        page.setFile(file);
        page.setActiveTabLabel(dataHolderLabel);
        page.setErrorMessage(errorMessage);

        show(page);
    }

    public void showDeleteFilePage(final TAFile file) {
        ConfirmationPage page = (ConfirmationPage) pages.get(DELETE_PAGE);
        page.setFile(file);
        page.setQuestionText("Delete this config?");
        page.setActionListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                services.getConfigStorage().delete(file);
                show(Navigator.CONFIGS);
            }
        });
        show(page);
    }

    public void showConfirmClonePage(final TAFile file) {
        ConfirmationPage page = (ConfirmationPage) pages.get(DELETE_PAGE);
        page.setFile(file);
        page.setQuestionText("Clone this config?");
        page.setActionListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String userLoginName = services.getAuthenticator().getUserName();
                services.getConfigStorage().cloneConfig(userLoginName, file);
                show(Navigator.CONFIGS);
            }
        });
        show(page);
    }

    private void checkLastAvailableVersion() {
        UpdateManager updateManager = new UpdateManager();

        if (updateManager.isCurrentVersionOutdated()) {
            updateMessage.setCaption("There's a newer version of Task Adapter available for download: " + updateManager.getLatestAvailableVersion()
                    + ". Your version:" + updateManager.getCurrentVersion());
        }
    }

    public void showError(String caption, String message) {
//        layout.getWindow().showNotification(caption, "<pre>" + message + "</pre>", Window.Notification.TYPE_ERROR_MESSAGE);
        showNotification(caption, message);
    }

    public void showNotification(String caption, String message) {
        layout.getWindow().showNotification(caption, "<pre>" + message + "</pre>", Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    public void addWindow(MessageDialog messageDialog) {
        layout.getApplication().getMainWindow().addWindow(messageDialog);
    }

    public Application getApplication() {
        return layout.getApplication();
    }

    public void updateLogoutButtonState() {
        header.updateLogoutButtonState();
    }

    /**
     * return to previous page
     */
    public void back() {
        if (previousPage != null)
            show(previousPage);
    }

    public Services getServices() {
        return services;
    }
}
