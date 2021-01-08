package com.taskadapter.webui;

import com.taskadapter.web.SettingsManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;

import java.util.Arrays;
import java.util.List;

class LocalRemoteOptionsPanel extends VerticalLayout {
    private static final String LOCAL = Page.message("configurePage.local");
    private static final String REMOTE = Page.message("configurePage.remote");

    private static final List<String> options = Arrays.asList(LOCAL, REMOTE);

    /**
     * Creates a new local/remote settings panel.
     *
     * @param settingsManager used settings manager.
     * @param canModify       if <code>true</code>, user can change the settings. Otherwise
     *                        user can see setting but cannot change it.
     * @return local/remote options panel.
     */
    public static Component createLocalRemoteOptions(
            final SettingsManager settingsManager, boolean canModify) {
        final VerticalLayout ui = new VerticalLayout(new Label());

        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel(Page.message("configurePage.caption"));
        group.setItems(options);
        group.setValue(settingsManager.isTAWorkingOnLocalMachine() ? LOCAL
                : REMOTE);
        group.addThemeVariants(RadioGroupVariant.MATERIAL_VERTICAL);
        group.setEnabled(canModify);

        if (canModify) {
            group.addValueChangeListener(event -> {
                boolean localModeRequested = event.getValue().equals(LOCAL);
                settingsManager.setLocal(localModeRequested);
                Notification.show(Page.message("configurePage.saved"));
            });
        }
        ui.add(group);
        return ui;
    }
}
