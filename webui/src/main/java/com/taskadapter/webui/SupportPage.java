package com.taskadapter.webui;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SupportPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    public SupportPage() {
        buildUI();
    }

    private void buildUI() {
        layout.setSpacing(true);
        Label textLabel = new Label("We will be happy to hear your comments or suggestions!");
        textLabel.setWidth(400, Sizeable.UNITS_PIXELS);
        layout.addComponent(textLabel);
        Link emailLink = new Link();
        emailLink.setResource(new ExternalResource("mailto:support@taskadapter.com"));
        emailLink.setCaption("Send us an email");
        emailLink.setTargetName("_new");
        layout.addComponent(emailLink);

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

    @Override
    public String getPageGoogleAnalyticsID() {
        return "support";
    }

    @Override
    public Component getUI() {
        return layout;
    }
}
