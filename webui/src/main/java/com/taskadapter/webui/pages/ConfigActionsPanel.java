package com.taskadapter.webui.pages;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Buttons panel with left/right arrows.
 */
public final class ConfigActionsPanel {
    private static final String NO_DESCRIPTION_TEXT = "<i>No description</i>"; // &nbsp;

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
        descriptionLayout
                .addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
                    @Override
                    public void layoutClick(LayoutClickEvent event) {
                        callback.edit(config);
                    }
                });

        final String labelText = mode.nameOf(config);
        final Label description = new Label(
                labelText.isEmpty() ? NO_DESCRIPTION_TEXT : labelText,
                ContentMode.HTML);
        description.addStyleName("configDescriptionLabel");
        descriptionLayout.addComponent(description);
        descriptionLayout.setComponentAlignment(description,
                Alignment.MIDDLE_LEFT);
        final Embedded editButton = new Embedded(null, new ThemeResource(
                "img/edit.png"));
        descriptionLayout.addComponent(editButton);
        descriptionLayout.setComponentAlignment(editButton,
                Alignment.MIDDLE_RIGHT);

        res.addComponent(descriptionLayout);

        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        horizontalLayout.addComponent(UniConfigExport.render(config,
                new Runnable() {
                    @Override
                    public void run() {
                        callback.forwardSync(config);
                    }
                }));
        horizontalLayout.addComponent(UniConfigExport.render(config.reverse(),
                new Runnable() {
                    @Override
                    public void run() {
                        callback.backwardSync(config);
                    }
                }));

        res.addComponent(horizontalLayout);
        return res;
    }
}
