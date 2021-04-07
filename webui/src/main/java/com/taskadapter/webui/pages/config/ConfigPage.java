package com.taskadapter.webui.pages.config;

import com.taskadapter.common.ui.ReloadableComponent;
import com.taskadapter.reporting.ErrorReporter;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.taskadapter.webui.pages.LayoutsUtil;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Config page with left/right arrows, connector names, action buttons (Delete/Clone/etc), fields mapping tab, etc.
 */
@Route(value = "config", layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class ConfigPage extends BasePage implements HasUrlParameter<String> {

    private final static Logger logger = LoggerFactory.getLogger(ConfigPage.class);
    private final ConfigOperations configOps = SessionController.buildConfigOperations();
    private final Preservices services = SessionController.getServices();
    private final Sandbox sandbox = SessionController.createSandbox();
    private final ErrorReporter errorReporter = SessionController.getErrorReporter();

    private final Map<Tab, ReloadableComponent> tabsToPages = new HashMap<>();

    private Tabs tabs;
    private Tab mappingsTab;
    private FieldMappingPanel fieldMappingsPanel;
    private OverviewPanel overviewPanel;
    private ResultsPanel previousResultsPanel;

    @Override
    public void setParameter(BeforeEvent event, String configIdStr) {
        removeAll();

        var configId = new ConfigId(SessionController.getCurrentUserName(), Integer.parseInt(configIdStr));
        var maybeConfig = configOps.getConfig(configId);
        if (maybeConfig.isPresent()) {
            var config = maybeConfig.get();
            showConfig(config);
        } else {
            logger.error("cannot find config with id " + configId);
        }
    }

    private void showConfig(UISyncConfig config) {
        var configTitleLine = new Label(config.getLabel());

        previousResultsPanel = new ResultsPanel(services, config.getConfigId());
        fieldMappingsPanel = new FieldMappingPanel(config, configOps);
        overviewPanel = new OverviewPanel(config, configOps, services, errorReporter, sandbox,
                error -> {
                    showMappingPanel(error);
                    return null;
                });

        var overviewTab = new Tab("Overview");
        mappingsTab = new Tab("Field mappings");
        var resultsTab = new Tab("Results");

        tabsToPages.put(overviewTab, overviewPanel);
        tabsToPages.put(mappingsTab, fieldMappingsPanel);
        tabsToPages.put(resultsTab, previousResultsPanel);

        tabs = new Tabs(
                overviewTab,
                mappingsTab,
                resultsTab
        );

        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addSelectedChangeListener(event -> {
            showSelectedTab(tabs.getSelectedTab());
        });
        overviewTab.setSelected(true);
        overviewPanel.reload();

        add(LayoutsUtil.centered(Sizes.mainWidth,
                configTitleLine,
                tabs,
                new Div(overviewPanel, fieldMappingsPanel, previousResultsPanel))
        );

        showSelectedTab(overviewTab);
    }

    private void showSelectedTab(Tab tab) {
        tabsToPages.values().forEach(page -> page.getComponent().setVisible(false));
        var selectedPage = tabsToPages.get(tab);
        selectedPage.reload();
        selectedPage.getComponent().setVisible(true);
    }

    private void showMappingPanel(String error) {
        tabs.setSelectedTab(mappingsTab);
        fieldMappingsPanel.showError(error);
    }

}