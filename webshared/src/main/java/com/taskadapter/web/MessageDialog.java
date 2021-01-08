package com.taskadapter.web;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class MessageDialog extends Dialog implements ComponentEventListener<ClickEvent<Button>> {

    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    private VerticalLayout view = new VerticalLayout();
    private Callback callback;

    public MessageDialog(String caption, String question, List<String> answers, Callback callback) {
//        super(caption);

        setModal(true);
        setCloseOnEsc(true);
//        addStyleName("not-maximizable-window");

        view.setSpacing(true);
        view.setMargin(true);

        this.callback = callback;

        if (question != null) {
            view.add(new Html(question));
            view.add(new Html("&nbsp;"));
        }

        createButtons(answers);
//        setContent(view);
    }

    private void createButtons(List<String> answers) {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);

        for (String answer : answers) {
//            Button button = new Button(answer, this);
//            buttonsLayout.add(button);
            // focus on something in this window so that the window can be closed with ESC
//            button.focus();
        }
        view.add(buttonsLayout);
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> event) {
        close();
        callback.onDialogResult(((Button) event.getSource()).getText());
    }

    public interface Callback {
        void onDialogResult(String answer);
    }
}

