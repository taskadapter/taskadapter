package com.taskadapter.webui.pages;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ImageLoader;
import com.taskadapter.webui.Page;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Buttons panel with left/right arrows.
 */
public final class ConfigActionsPanel {

    /**
     * Renders a config actions panel.
     * 
     * @param config
     *            config to render.
     * @param mode
     *            rendering mode.
     * @param callback
     *            item callback.
     * @return UI component.
     */
    public static Component render(final UISyncConfig config,
            ConfigsPage.DisplayMode mode, final ConfigsPage.Callback callback) {
        final VerticalLayout res = new VerticalLayout();

        res.addStyleName("configPanelInConfigsList");

        final HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setWidth(100, Unit.PERCENTAGE);
        descriptionLayout.addStyleName("configDescriptionPanel");
        descriptionLayout.addLayoutClickListener((LayoutEvents.LayoutClickListener) event -> callback.edit(config));

        final String labelText = mode.nameOf(config);
        final Label description = new Label(
                labelText.isEmpty() ? Page.message("configsPage.noDescription") : labelText,
                ContentMode.HTML);
        description.addStyleName("configDescriptionLabel");
        descriptionLayout.addComponent(description);
        descriptionLayout.setComponentAlignment(description,
                Alignment.MIDDLE_LEFT);
        final Embedded editButton = new Embedded(null, ImageLoader.getImage("edit.png"));
        descriptionLayout.addComponent(editButton);
        descriptionLayout.setComponentAlignment(editButton,
                Alignment.MIDDLE_RIGHT);

        res.addComponent(descriptionLayout);

        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        horizontalLayout.addComponent(UniConfigExport.render(config,
                new UniConfigExport.Callback() {
                    @Override
                    public void dropInExport(Html5File file) {
                        callback.forwardDropIn(config, file);
                    }

                    @Override
                    public void doExport() {
                        callback.forwardSync(config);
                    }
                }));
        horizontalLayout.addComponent(UniConfigExport.render(config.reverse(),
                new UniConfigExport.Callback() {
                    @Override
                    public void dropInExport(Html5File file) {
                        callback.backwardDropIn(config, file);
                    }

                    @Override
                    public void doExport() {
                        callback.backwardSync(config);
                    }
                }));

        res.addComponent(horizontalLayout);
        return res;
    }
}
