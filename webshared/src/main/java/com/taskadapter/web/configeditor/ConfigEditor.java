package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigEditor extends VerticalLayout implements WindowProvider {
    private List<Validatable> toValidate = new ArrayList<Validatable>();

    // TODO the parent editor class must save / load data itself instead of letting the children do this

    private final ConfigPanelContainer panelContainer = new ConfigPanelContainer();

    protected ConnectorConfig config;
    protected Services services;

    private static final String LABEL_DESCRIPTION_TEXT = "Description:";
    private static final String LABEL_TOOLTIP = "Text to show for this connector on 'Export' button. Enter any text.";
    private final HorizontalLayout descriptionLayout;

    ConfigEditor(ConnectorConfig config, Services services) {
        this.config = config;
        this.services = services;
        setImmediate(false);
        setMargin(true);
        setSpacing(true);

        descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        addComponent(descriptionLayout);
        descriptionLayout.addComponent(new Label(LABEL_DESCRIPTION_TEXT));
        TextField labelText = new TextField();
        labelText.setDescription(LABEL_TOOLTIP);
        labelText.addStyleName("label-textfield");
        labelText.setPropertyDataSource(new MethodProperty<String>(config,
                "label"));
        descriptionLayout.addComponent(labelText);
        setWidth("840px");
    }

    protected void addPanelToLayout(Layout component, Panel panel) {
        //if layout supports Validatable interface add it to validation list
        if (panel instanceof Validatable) {
            toValidate.add((Validatable) panel);
        }

        component.addComponent(panel);
        panelContainer.add(panel);
    }

    public void validateAll() throws ValidationException {
        for (Validatable v : toValidate) {
            v.validate();
        }
        validate();
    }

    /**
     * the default implementation does nothing.
     */
    public void validate() throws ValidationException {
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public <T> T getPanel(Class<T> clazz) {
        return panelContainer.get(clazz);
    }

    // TODO this is an intermediate step in editors refactoring:
    // Description field is migrating into "Server Panel". Redmine Editor already has this field
    // in RedmineServerPanel and needs to hide this generic field.
    // we will delete Description field from here when all editors have this field in some Server Panel.
    protected void hideDescription() {
        descriptionLayout.setVisible(false);
    }
}
