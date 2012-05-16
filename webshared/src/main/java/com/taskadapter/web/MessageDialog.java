package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import java.util.List;

public class MessageDialog extends Window implements Button.ClickListener {

    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    private HorizontalLayout layout = new HorizontalLayout();
    private Callback callback;

    public MessageDialog(String caption, String question, List<String> answers, Callback callback) {
        super(caption);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        layout.setSpacing(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
            addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        }

        createButtons(answers);
        addComponent(layout);
    }

    private void createButtons(List<String> answers) {
        for (String answer : answers) {
            Button button = new Button(answer, this);
            layout.addComponent(button);
            // focus on something in this window so that the window can be closed with ESC
            button.focus();
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

