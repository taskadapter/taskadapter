package com.taskadapter.webui.pages;

import com.taskadapter.vaadin14shim.VerticalLayout;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.taskadapter.vaadin14shim.GridLayout;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.web.SettingsManager;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.io.IOException;

public final class LicenseAgreementPage {

    private static final String AGREEMENT_TITLE = "License Agreement";
    private static final String AGREEMENT_FILE_NAME = "license.html";
    private static final String AGREEMENT_FILE_NOT_FOUND = "License agreement not found.";
    private static final String ACCEPT_CHECKBOX = "I have read the license agreement and I accept it.";
    private static final String ACCEPT_BUTTON = "Accept";

    private static final String FORM_WIDTH = "700px";
    private static final String AGREEMENT_PANEL_HEIGHT = "500px";

    /**
     * Renders a license agreement page.
     * @param sm settings manager.
     * @param onComplete page completion handler.
     * @return license agreement page.
     */
    public static Component render(final SettingsManager sm, final Runnable onComplete) {
        final Panel panel = new Panel(AGREEMENT_TITLE);
        
        final Panel agreementPanel = new Panel();
        agreementPanel.setWidth(FORM_WIDTH);
        agreementPanel.setHeight(AGREEMENT_PANEL_HEIGHT);

        final Label agreementContent = new Label(readLicense());        
        agreementContent.setContentMode(ContentMode.HTML);
        agreementPanel.setContent(agreementContent);

        final HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setWidth(FORM_WIDTH);
        actionLayout.setMargin(new MarginInfo(true, false, false, false));

        final CheckBox acceptCheckbox = new CheckBox(ACCEPT_CHECKBOX);
        acceptCheckbox.setValue(false);
        acceptCheckbox.setImmediate(true);

        Button acceptButton = new Button(ACCEPT_BUTTON, event -> {
            sm.markLicenseAgreementAsAccepted();
            onComplete.run();
        });
        acceptButton.setEnabled(false);

        acceptCheckbox
                .addValueChangeListener(new CheckBox.ValueChangeListener() {
                    @Override
                    public void valueChange(
                            Property.ValueChangeEvent valueChangeEvent) {
                        acceptButton.setEnabled(acceptCheckbox.getValue());
                    }
                });
        actionLayout.add(acceptCheckbox);
        actionLayout.setComponentAlignment(acceptCheckbox,
                Alignment.MIDDLE_LEFT);

        actionLayout.add(acceptButton);
        actionLayout
                .setComponentAlignment(acceptButton, Alignment.MIDDLE_RIGHT);
        
        final VerticalLayout view = new VerticalLayout();
        view.add(agreementPanel);
        view.add(actionLayout);

        panel.setContent(view);
        
        return panel;
    }

    /**
     * Reads a license agreement.
     * @return license agreement.
     */
    private static String readLicense() {
        try {
            return Resources.toString(
                    Resources.getResource(AGREEMENT_FILE_NAME), Charsets.UTF_8);
        } catch (IOException e) {
            return AGREEMENT_FILE_NOT_FOUND;
        }
    }
}
