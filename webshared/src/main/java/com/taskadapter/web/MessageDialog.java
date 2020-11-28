package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.vaadin.ui.Label;
import com.taskadapter.vaadin14shim.VerticalLayout;
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
        addStyleName("not-maximizable-window");

        view.setSpacing(true);
        view.setMargin(true);

        this.callback = callback;

        if (question != null) {
            view.add(new Label(question, ContentMode.HTML));
            view.add(new Label("&nbsp;", ContentMode.HTML));
        }

        createButtons(answers);
        setContent(view);
    }

    private void createButtons(List<String> answers) {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);

        for (String answer : answers) {
            Button button = new Button(answer, this);
            buttonsLayout.add(button);
            // focus on something in this window so that the window can be closed with ESC
            button.focus();
        }
        view.add(buttonsLayout);
    }

    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {
        getUI().removeWindow(this);
        callback.onDialogResult(((Button) clickEvent.getSource()).getCaption());
    }

    public interface Callback {
        void onDialogResult(String answer);
    }
}

