package com.taskadapter.web;

import com.taskadapter.webui.Page;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.function.Function;

public class PopupDialog extends Dialog {
    public static final String YES_LABEL = Page.message("popupDialog.buttonYes");
    public static final String CANCEL_LABEL = Page.message("popupDialog.buttonCancel");
    private final VerticalLayout view;
    private final Function<String, Void> clicked;

    public static void confirm(String question, Runnable confirmedAction) {
        new PopupDialog(/*Page.message("popupDialog.confirmationCaption"),*/
                question, List.of(YES_LABEL, CANCEL_LABEL), action -> {
            if (action.equals(YES_LABEL)) {
                confirmedAction.run();
            }
            return null;
        }).open();
    }

    public PopupDialog(String question, List<String> answers, Function<String, Void> clicked) {
        this.clicked = clicked;
        setModal(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        // TODO delete "not maximizable" stype

        view = new VerticalLayout();
        view.setSpacing(true);
        view.setMargin(true);
        var label = new Span();
        label.getElement().setProperty("innerHTML", question);
        label.setWidth("300px");
        view.add(label);

//  view.add(new Label("&nbsp;", ContentMode.HTML));
        createButtons(answers);

        add(view);
    }

    private void createButtons(List<String> answers) {
        var buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        for (String answer : answers) {
            var button = new Button(answer, event -> {
                close();
                clicked.apply((event.getSource()).getText());
            });
            buttonsLayout.add(button);
            // focus on something in this window so that the window can be closed with ESC
            button.focus();
        }
        view.add(buttonsLayout);
    }
}
