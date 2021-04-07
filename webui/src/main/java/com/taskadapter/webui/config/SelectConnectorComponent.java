package com.taskadapter.webui.config;

import com.taskadapter.PluginManager;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Function;

public class SelectConnectorComponent extends VerticalLayout {

    private final PluginManager pluginManager;
    private final Function<String, Void> next;

    public SelectConnectorComponent(PluginManager pluginManager, Function<String, Void> next) {
        this.pluginManager = pluginManager;
        this.next = next;
        setSpacing(true);
        setMargin(true);
        add(new Label(Page.message("newConfig.selectSystem")));

        createSystemList();
    }

    private void createSystemList() {
        pluginManager.getPluginDescriptors().forEachRemaining(connector -> {
            var systemButton = new Button(connector.getLabel(),
                    e -> next.apply(connector.getId()));
            systemButton.setWidth("200px");
            add(systemButton);
        });
    }
}
