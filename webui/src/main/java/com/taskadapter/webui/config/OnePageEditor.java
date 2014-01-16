package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public final class OnePageEditor {

    private final VerticalLayout layout;
    private final TaskFieldsMappingFragment taskFieldsMappingFragment;

    public OnePageEditor(Messages messages, Sandbox sandbox,
            UISyncConfig config, Runnable exportToLeft, Runnable exportToRight) {

        this.layout = new VerticalLayout();
        layout.setWidth(760, Unit.PIXELS);
        layout.setMargin(true);

        final HorizontalLayout connectorsLayout = new HorizontalLayout();

        addConnectorPanel(connectorsLayout, config.getConnector1(), sandbox,
                Alignment.MIDDLE_RIGHT);

        final Component exportButtonsFragment = ExportButtonsFragment.render(
                messages, exportToLeft, exportToRight);
        connectorsLayout.addComponent(exportButtonsFragment);
        connectorsLayout.setComponentAlignment(exportButtonsFragment,
                Alignment.MIDDLE_CENTER);
        addConnectorPanel(connectorsLayout, config.getConnector2(), sandbox,
                Alignment.MIDDLE_LEFT);

        layout.addComponent(connectorsLayout);

        taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages,
                config.getConnector1(), config.getConnector2(),
                config.getNewMappings());
        layout.addComponent(taskFieldsMappingFragment.getUI());
    }

    private static void addConnectorPanel(HorizontalLayout layout,
            UIConnectorConfig config, Sandbox sandbox, Alignment align) {
        final MiniPanel miniPanel2 = createMiniPanel(config, sandbox);
        layout.addComponent(miniPanel2);
        layout.setComponentAlignment(miniPanel2, align);
    }

    private static MiniPanel createMiniPanel(UIConnectorConfig connectorConfig,
            Sandbox sandbox) {
        final MiniPanel miniPanel = new MiniPanel(connectorConfig);
        // "services" instance is only used by MSP Editor Factory
        miniPanel.setPanelContents(connectorConfig.createMiniPanel(sandbox));
        return miniPanel;
    }

    public void validate() throws BadConfigException {
        // TODO !!! validate left/right editors too. this was lost during the
        // last refactoring.
        taskFieldsMappingFragment.validate();
    }
    
    /**
     * Returns page editor UI.
     * @return page editor UI.
     */
    public Component getUI() {
        return layout;
    }
}
