package com.taskadapter.webui;

import com.taskadapter.web.service.UpdateManager;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SupportPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private UpdateManager updateManager;

    public SupportPage(UpdateManager updateManager) {
        this.updateManager = updateManager;
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        addCurrentVersion();
        addCheckForUpdateLink();
        addEmailLink();

/*        Form feedbackForm = new Form();
        feedbackForm.setDescription("You can send your comments to Task Adapter developers using this form");
        feedbackForm.setImmediate(true);
        feedbackForm.setFooter(new VerticalLayout());
        feedbackForm.getFooter().addComponent(new Label("All fields are required"));

        EmailValidator emailSenderValidator = new EmailValidator("Invalid e-mail address entered.");

        HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setHeight("26px");
        feedbackForm.getFooter().addComponent(buttonsBar);

        TextField nameSender = new TextField(FIELD_NAME);
        nameSender.setColumns(30);
        nameSender.setRequired(true);
        nameSender.setRequiredError("Name is missing");
        TextField emailSender = new TextField(FIELD_EMAIL);
        emailSender.setColumns(30);
        emailSender.setRequired(true);
        emailSender.setRequiredError("E-mail address is missing");
        emailSender.addValidator(emailSenderValidator);
        TextArea emailMessage = new TextArea(FIELD_MESSAGE);
        emailMessage.setColumns(30);
        emailMessage.setRows(10);
        emailMessage.setRequired(true);
        emailMessage.setRequiredError("Message is missing");

        feedbackForm.addField(FIELD_NAME, nameSender);
        feedbackForm.addField(FIELD_EMAIL, emailSender);
        feedbackForm.addField(FIELD_MESSAGE, emailMessage);

        Button sendButton = new Button("Send", feedbackForm, "commit");
        buttonsBar.addComponent(sendButton);
        buttonsBar.setComponentAlignment(sendButton, Alignment.TOP_RIGHT);
        buttonsBar.addComponent(new Button("Cancel"));

        layout.addComponent(feedbackForm);*/
    }

    private void addCurrentVersion() {
        Label currentVersionLabel = new Label("Task Adapter version " + updateManager.getCurrentVersion());
        currentVersionLabel.setWidth(400, Sizeable.UNITS_PIXELS);
        layout.addComponent(currentVersionLabel);
    }

    private void addCheckForUpdateLink() {
        Link link = new Link();
        link.setResource(new ExternalResource("http://www.taskadapter.com/download"));
        link.setCaption("Check for update (sorry, manually for now)");
        link.setTargetName("_new");
        layout.addComponent(link);
    }

    private void addEmailLink() {
        Link emailLink = new Link();
        emailLink.setResource(new ExternalResource("mailto:support@taskadapter.com"));
        emailLink.setCaption("Send us an email");
        emailLink.setTargetName("_new");
        layout.addComponent(emailLink);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "support";
    }

    @Override
    public Component getUI() {
        return layout;
    }
}
