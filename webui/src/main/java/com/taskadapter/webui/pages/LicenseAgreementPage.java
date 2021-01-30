package com.taskadapter.webui.pages;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.EventTracker;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.SessionController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

import java.io.IOException;

@Route(value = Navigator.LICENSE, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class LicenseAgreementPage extends BasePage {

    private static final String AGREEMENT_TITLE = "Software Usage Agreement";
    private static final String AGREEMENT_FILE_NAME = "license.html";
    private static final String AGREEMENT_FILE_NOT_FOUND = "License agreement not found.";
    private static final String ACCEPT_CHECKBOX = "I have read the license agreement and I accept it.";
    private static final String ACCEPT_BUTTON = "Accept";

    private static final String FORM_WIDTH = "700px";
    private static final String AGREEMENT_PANEL_HEIGHT = "500px";

    public LicenseAgreementPage() {
        Label agreementContent = new HtmlLabel(readLicense());

        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setWidth(FORM_WIDTH);

        Checkbox acceptCheckbox = new Checkbox(ACCEPT_CHECKBOX);
        acceptCheckbox.setValue(false);

        SettingsManager settingsManager = SessionController.getServices().settingsManager;
        Button acceptButton = new Button(ACCEPT_BUTTON, event -> {
            settingsManager.markLicenseAgreementAsAccepted();
            Navigator.home();
        });
        acceptButton.setEnabled(false);

        acceptCheckbox
                .addClickListener(e -> acceptButton.setEnabled(acceptCheckbox.getValue()));
        actionLayout.add(acceptCheckbox);

        actionLayout.add(acceptButton);

        setWidth(FORM_WIDTH);
        setHeight(AGREEMENT_PANEL_HEIGHT);

        add(
                new H1(AGREEMENT_TITLE),
                agreementContent,
                actionLayout);
    }

    private static String readLicense() {
        try {
            return Resources.toString(
                    Resources.getResource(AGREEMENT_FILE_NAME), Charsets.UTF_8);
        } catch (IOException e) {
            return AGREEMENT_FILE_NOT_FOUND;
        }
    }
}
