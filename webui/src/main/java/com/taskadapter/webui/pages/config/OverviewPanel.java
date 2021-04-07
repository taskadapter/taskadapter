package com.taskadapter.webui.pages.config;

import com.taskadapter.Constants;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.common.ui.ReloadableComponent;
import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.exception.FieldNotMappedException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.reporting.ErrorReporter;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigActionsFragment;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.pages.ExportPage;
import com.taskadapter.webui.pages.ModalWindow;
import com.taskadapter.webui.pages.ValidationErrorTextWithProcessor;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OverviewPanel extends VerticalLayout implements ReloadableComponent {
    private final static String height = "100px";

    private final static Logger log = LoggerFactory.getLogger(OverviewPanel.class);

    private final UISyncConfig config;
    private final ConfigOperations configOps;
    private final Preservices services;
    private final ErrorReporter errorReporter;
    private final ConfigId configId;
    private final Sandbox sandbox;
    private final Function<String, Void> showMappingPanelError;
    private Button rightButton;
    private Button leftButton;
    private ValidationMessagesPanel validationPanelSaveToRight;
    private ValidationMessagesPanel validationPanelSaveToLeft;
    private HorizontalLayout horizontalLayout;

    OverviewPanel(UISyncConfig config, ConfigOperations configOps, Preservices services,
                  ErrorReporter errorReporter,
                  Sandbox sandbox,
                  Function<String, Void> showMappingPanelError) {
        this.config = config;
        this.configOps = configOps;
        this.services = services;
        this.errorReporter = errorReporter;
        this.configId = config.getConfigId();
        this.sandbox = sandbox;
        this.showMappingPanelError = showMappingPanelError;
        buildUi();
    }

    private Runnable configSaver = new Runnable() {
        public void run() {
            // TODO 14 check that the new config is used here
            try {
                configOps.saveConfig(config);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            recreateContents(config);
        }
    };

    private void exportCommon(UISyncConfig config) {
        log.info(String.format("Starting export\n"
                        + "from %1$s ( %2$s )"
                        + "to   %3$s ( %4$s )",
                config.getConnector1().getConnectorTypeId(), config.getConnector1().getSourceLocation(),
                config.getConnector2().getConnectorTypeId(), config.getConnector2().getDestinationLocation())
        );

        var maxTasks = services.licenseManager.isSomeValidLicenseInstalled() ?
                Constants.maxTasksToLoad : LicenseManager.TRIAL_TASKS_NUMBER_LIMIT;

        log.info("License installed? " + services.licenseManager.isSomeValidLicenseInstalled());
        var panel = new ExportPage(getUI().get(), services.exportResultStorage, config, maxTasks,
                services.settingsManager.isTAWorkingOnLocalMachine(),
                () -> showInitialState(),
                configOps,
                errorReporter);
        removeAll();
        add(panel);
        panel.startLoading();
    }

    private void buildUi() {
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setHeight(height);
        horizontalLayout.setPadding(true);

        validationPanelSaveToRight = new ValidationMessagesPanel(
                Page.message("configSummary.validationPanelCaption", config.getConnector2().getLabel()));
        validationPanelSaveToLeft = new ValidationMessagesPanel(
                Page.message("configSummary.validationPanelCaption", config.getConnector1().getLabel()));

        rightButton = createArrow(VaadinIcon.ARROW_RIGHT, e -> {
            sync(reloadConfig(config.getConfigId()));
        });

        leftButton = createArrow(VaadinIcon.ARROW_LEFT, e -> {
            sync(reloadConfig(config.getConfigId()).reverse());
        });
    }

    private Button createArrow(VaadinIcon icon,
                               ComponentEventListener<ClickEvent<Button>> listener) {
        var arrow = icon.create();
        var button = new Button(arrow);
        button.setHeight("40px");
        button.setWidth("120px");
        button.getElement().setProperty("title", Page.message("export.exportButtonTooltip"));
        button.addClickListener(listener);
        return button;
    }

    /**
     * reload config from disk in case it was changed in another UI panel, or maybe even externally
     */
    private UISyncConfig reloadConfig(ConfigId configId) {
        var maybeConfig = configOps.getConfig(configId);
        if (maybeConfig.isEmpty()) {
            throw new RuntimeException("Config with id " + configId + " is not found");
        }
        return maybeConfig.get();
    }

    /**
     * Performs a synchronization operation from first connector to second.
     *
     * @param config base config. May be saved!
     */
    private void sync(UISyncConfig config) {
        exportCommon(config);
    }

    private void showEditConnectorDialog(UIConnectorConfig connectorConfig,
                                         Runnable configSaver,
                                         Sandbox sandbox) {
        var systemPanel = connectorConfig.createMiniPanel(sandbox);
        var dialog = ModalWindow.showDialog(systemPanel.getComponent());
        dialog.addDialogCloseActionListener(e -> {
            // save the fields from the component into the original bean
            systemPanel.save();

            // save the config to disk
            configSaver.run();

            dialog.close();
        });
    }

    private void recreateContents(UISyncConfig config) {

        var leftConnectorEditListener = new Runnable() {
            public void run() {
                showEditConnectorDialog(config.getConnector1(), configSaver, sandbox);
            }
        };

        var leftSystemButton = createConfigureConnectorButton(config.getConnector1(), leftConnectorEditListener);

        var leftRightButtonsPanel = new VerticalLayout();
        leftRightButtonsPanel.add(rightButton);
        leftRightButtonsPanel.add(leftButton);
        leftRightButtonsPanel.setWidth("120px");
        leftRightButtonsPanel.setHeight(height);
        leftRightButtonsPanel.setPadding(false);

        var rightConnectorEditListener = new Runnable() {
            public void run() {
                showEditConnectorDialog(config.getConnector2(), configSaver, sandbox);
            }
        };
        var rightSystemButton = createConfigureConnectorButton(config.getConnector2(), rightConnectorEditListener);

        horizontalLayout.removeAll();
        horizontalLayout.addAndExpand(
                leftSystemButton,
                leftRightButtonsPanel,
                rightSystemButton);

        horizontalLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER,
                leftSystemButton,
                leftRightButtonsPanel,
                rightSystemButton);

        performValidation(config, configSaver);
    }

    private Component createConfigureConnectorButton(
            UIConnectorConfig connectorConfig,
            Runnable buttonListener) {
        var iconResource = VaadinIcon.EDIT;
        var button = new Button(connectorConfig.getLabel());
        button.setIcon(iconResource.create());
        button.setWidth("400px");
        button.setHeight(height);
        button.addClickListener(e -> buttonListener.run());
        return button;
    }

    private void showInitialState() {
        reload();
    }

    private void performValidation(UISyncConfig config, Runnable configSaver) {
        var errorsSaveToLeft = validateSaveToLeft(config, configSaver);
        leftButton.setEnabled(errorsSaveToLeft.isEmpty());
        validationPanelSaveToLeft.show(errorsSaveToLeft);

        var errorsSaveToRight = validateSaveToRight(config, configSaver);
        rightButton.setEnabled(errorsSaveToRight.isEmpty());
        validationPanelSaveToRight.show(errorsSaveToRight);
    }

    private List<ValidationErrorTextWithProcessor> validateSaveToLeft(UISyncConfig config, Runnable configSaver) {
        var loadErrors = validateLoad(config.getConnector2(), config.getFieldMappings(), configSaver);
        var saveErrors = validateSave(config.getConnector1(), config.getFieldMappings(), configSaver);
        loadErrors.addAll(saveErrors);
        return loadErrors;
    }

    private List<ValidationErrorTextWithProcessor> validateSaveToRight(UISyncConfig config, Runnable configSaver) {
        var loadErrors = validateLoad(config.getConnector1(), config.getFieldMappings(), configSaver);
        var saveErrors = validateSave(config.getConnector2(), config.getFieldMappings(), configSaver);
        loadErrors.addAll(saveErrors);
        return loadErrors;
    }

    private List<ValidationErrorTextWithProcessor> validateSave(UIConnectorConfig uiConfig,
                                                                List<FieldMapping<?>> fieldMappings,
                                                                Runnable configSaver) {
        var errors = uiConfig.validateForSave(fieldMappings);
        return errors.stream().map(e -> buildItem(uiConfig, e, configSaver))
                .collect(Collectors.toList());
    }

    private List<ValidationErrorTextWithProcessor> validateLoad(UIConnectorConfig uiConfig,
                                                                List<FieldMapping<?>> fieldMappings,
                                                                Runnable configSaver) {
        var errors = uiConfig.validateForLoad();
        return errors.stream().map(e -> buildItem(uiConfig, e, configSaver))
                .collect(Collectors.toList());
    }

    private ValidationErrorTextWithProcessor buildItem(UIConnectorConfig uiConfig,
                                                       BadConfigException e,
                                                       Runnable configSaver) {
        return new ValidationErrorTextWithProcessor(uiConfig.decodeException(e), buildFixProcessor(uiConfig, e, configSaver));
    }

    private Runnable buildFixProcessor(UIConnectorConfig uiConnectorConfig, BadConfigException e, Runnable configSaver) {
        return new Runnable() {
            @Override
            public void run() {
                if (e instanceof FieldNotMappedException) {
                    showMappingPanelError.apply(uiConnectorConfig.decodeException(e));
                } else {
                    showEditConnectorDialog(uiConnectorConfig, configSaver, sandbox);
                }
            }
        };
    }

    @Override
    public void reload() {
        removeAll();
        var buttonsLayout = new ConfigActionsFragment(configId);

        add(buttonsLayout,
                horizontalLayout,
                validationPanelSaveToRight,
                validationPanelSaveToLeft);

        recreateContents(config);
    }

    public Component getComponent() {
        return this;
    }
}