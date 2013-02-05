package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.List;

public class MessageDialog extends Window implements Button.ClickListener {

    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    private VerticalLayout view = new VerticalLayout();
    private Callback callback;

    public MessageDialog(String caption, String question, List<String> answers, Callback callback) {
        super(caption);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        view.setSpacing(true);
        view.setMargin(true);

        this.callback = callback;

        if (question != null) {
            view.addComponent(new Label(question, ContentMode.HTML));
            view.addComponent(new Label("&nbsp;", ContentMode.HTML));
        }

        createButtons(answers);
        setContent(view);
    }

    private void createButtons(List<String> answers) {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);

        for (String answer : answers) {
            Button button = new Button(answer, this);
            buttonsLayout.addComponent(button);
            // focus on something in this window so that the window can be closed with ESC
            button.focus();
        }
        view.addComponent(buttonsLayout);
    }

    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {
        getUI().removeWindow(this);
        callback.onDialogResult(((Button) clickEvent.getSource()).getCaption());
    }

    public interface Callback {
        public void onDialogResult(String answer);
    }
}

