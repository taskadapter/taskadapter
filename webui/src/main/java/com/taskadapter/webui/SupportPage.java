package com.taskadapter.webui;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;

public class SupportPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    private static final String FIELD_NAME = "Your Name";
    private static final String FIELD_EMAIL = "Your E-mail";
    private static final String FIELD_MESSAGE = "Message";

    public SupportPage() {
        buildUI();
    }

    private void buildUI() {

        Form feedbackForm = new Form();
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

        layout.addComponent(feedbackForm);
    }

    @Override
    public String getPageTitle() {
        return "Feedback";
    }

    @Override
    public Component getUI() {
        return layout;
    }
}
