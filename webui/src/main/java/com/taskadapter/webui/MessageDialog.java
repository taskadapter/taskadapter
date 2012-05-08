package com.taskadapter.webui;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import java.util.List;

public class MessageDialog extends Window implements Button.ClickListener {

    private HorizontalLayout layout = new HorizontalLayout();
    private Callback callback;

    public MessageDialog(String caption, String question, List<String> answers, Callback callback) {
        super(caption);

        setModal(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        createButtons(answers);
        addComponent(layout);
    }

    private void createButtons(List<String> answers) {
        for (String answer : answers) {
            Button yes = new Button(answer, this);
            layout.addComponent(yes);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {
        if (getParent() != null) {
            (getParent()).removeWindow(this);
        }
        callback.onDialogResult(((Button) clickEvent.getSource()).getCaption());
    }

    public interface Callback {
        public void onDialogResult(String answer);
    }
}

