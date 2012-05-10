package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.service.UpdateManager;
import com.taskadapter.webui.license.ConfigureSystemPage;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

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

    private static final String TASK_DETAILS_PAGE = "task_details";
    private static final String LOGIN_PAGE = "login_page";
    private static final String DELETE_PAGE = "delete_task";

    private Map<String, Page> pages = new HashMap<String, Page>();

    private HorizontalLayout navigationPanel;
    private HorizontalLayout currentComponentArea = new HorizontalLayout();
    private HorizontalLayout mainArea = new HorizontalLayout();
    private VerticalLayout layout;
    private Services services;
    private Label updateMessage;

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
        registerPage(TASK_DETAILS_PAGE, new ConfigDetailsPage());
        registerPage(DELETE_PAGE, new ConfirmationPage());
    }

    private void buildUI() {
        Header header = new Header(this);
        header.setHeight(50, Sizeable.UNITS_PIXELS);
        header.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        layout.addComponent(header);

        addUpdatePanel();
        addNavigationPanel();

        mainArea.addComponent(currentComponentArea);
        mainArea.setWidth("1020px");
        mainArea.setComponentAlignment(currentComponentArea, Alignment.MIDDLE_CENTER);
        layout.addComponent(mainArea);
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

    public void show(Page page) {
        setServicesToPage(page);
        /* // TODO: uncomment for production
            if (!authenticator.isLoggedIn()) {
                show(pages.get(LOGIN_PAGE));
            } else {
                show(page);
            }
        */
        currentComponentArea.removeAllComponents();
        Component ui = page.getUI();
        ui.setSizeUndefined();
        currentComponentArea.addComponent(ui);
        currentComponentArea.setComponentAlignment(ui, Alignment.TOP_LEFT);

        navigationPanel.removeAllComponents();

        Label titleLabel = new Label(page.getPageTitle());
        titleLabel.setSizeUndefined();
        navigationPanel.addComponent(titleLabel);
    }

    private void setServicesToPage(Page page) {
        page.setNavigator(this);
        page.setServices(services);
    }

    // TODO these 4 showXX methods are not in line with the other show(). refactor!
    public void showConfigureTaskPage(TAFile file) {
        showConfigureTaskPage(file, null, null);
    }

    public void showConfigureTaskPage(TAFile file, String dataHolderLabel, String errorMessage) {
        ConfigureTaskPage page = (ConfigureTaskPage) pages.get(CONFIGURE_TASK_PAGE);
        page.setFile(file);
        page.setActiveTabLabel(dataHolderLabel);
        page.setErrorMessage(errorMessage);

        show(page);
    }

    public void showTaskDetailsPage(TAFile file) {
        ConfigDetailsPage page = (ConfigDetailsPage) pages.get(TASK_DETAILS_PAGE);
        page.setFile(file);
        show(page);
    }

    public void showDeleteFilePage(final TAFile file) {
        ConfirmationPage page = (ConfirmationPage) pages.get(DELETE_PAGE);
        page.setFile(file);
        page.setQuestionText("Delete config '" + file.getConfigLabel() + "' ?");
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
        page.setQuestionText("Clone config '" + file.getConfigLabel() + "' ?");
        page.setActionListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String clonedConfigLabel = "Copy of " + file.getConfigLabel();
                services.getConfigStorage().cloneConfig(file, clonedConfigLabel);
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
}
