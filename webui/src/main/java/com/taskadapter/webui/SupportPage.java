package com.taskadapter.webui;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.*;

public class SupportPage extends Page {

    private static String[] fields = {"Name", "E-mail", "Message"};

    public SupportPage() {
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();

        Form feedbackForm = new Form();
        feedbackForm.setDescription("You can send your comments to Task Adapter developers using this form");
        feedbackForm.setImmediate(true);
        feedbackForm.setFooter(new VerticalLayout());
        feedbackForm.getFooter().addComponent(new Label("All fields are required"));

        EmailValidator emailSenderValidator = new EmailValidator("Invalid e-mail address entered. Please correct it and try again");

        HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setHeight("26px");
        feedbackForm.getFooter().addComponent(buttonsBar);

        TextField nameSender = new TextField(fields[0]);
        nameSender.setColumns(30);
        nameSender.setRequired(true);
        nameSender.setRequiredError("Name is missing");
        TextField emailSender = new TextField(fields[1]);
        emailSender.setColumns(30);
        emailSender.setRequired(true);
        emailSender.setRequiredError("E-mail address is missing");
        emailSender.addValidator(emailSenderValidator);
        TextArea emailMessage = new TextArea(fields[2]);
        emailMessage.setColumns(30);
        emailMessage.setRows(10);
        emailMessage.setRequired(true);
        emailMessage.setRequiredError("Message is missing");

        feedbackForm.addField(fields[0], nameSender);
        feedbackForm.addField(fields[1], emailSender);
        feedbackForm.addField(fields[2], emailMessage);

        Button sendFeedBackBtn = new Button("Send", feedbackForm, "commit");

        buttonsBar.addComponent(sendFeedBackBtn);
        buttonsBar.setComponentAlignment(sendFeedBackBtn, Alignment.TOP_RIGHT);
        buttonsBar.addComponent(new Button("Reset", feedbackForm, "discard"));
        buttonsBar.addComponent(new Button("Cancel"));


        layout.addComponent(feedbackForm);

        setCompositionRoot(layout);
    }


    @Override
    public String getPageTitle() {
        return "FeedBack";
    }
}
