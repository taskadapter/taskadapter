package com.taskadapter.web;

import com.taskadapter.vaadin14shim.HorizontalLayout;
import com.taskadapter.vaadin14shim.VerticalLayout;
import com.taskadapter.vaadin14shim.Button;
import com.taskadapter.vaadin14shim.Label;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;


public class InputDialog extends Window {

    private HorizontalLayout textFieldLayout = new HorizontalLayout();
    private AbstractTextField textField;

    public InputDialog(String caption, String question, final Recipient recipient) {
        super(caption);

        VerticalLayout view = new VerticalLayout();
        setContent(view);
        view.setSizeUndefined();
        view.setMargin(true);

        setModal(true);
        addStyleName("not-maximizable-window");
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);

        HorizontalLayout textLayout = new HorizontalLayout();
        textLayout.setSpacing(true);
        textLayout.add(new Label(question, ContentMode.HTML));
        textLayout.add(new Label("&nbsp;", ContentMode.HTML));
        textLayout.add(textFieldLayout);
        view.add(textLayout);

        final Window dialog = this;

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(true, false, false, false));

        Button okButton = new Button("Ok", event -> {
                recipient.gotInput(textField.getValue());
                getUI().removeWindow(dialog);
        });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addClassName("v-button-default");
        layout.add(okButton);

        Button cancelButton = new Button("Cancel", event -> getUI().removeWindow(dialog));
        layout.add(cancelButton);
        view.add(layout);
        view.setComponentAlignment(layout, Alignment.BOTTOM_CENTER);
        setPlainTextMode();
    }

    public interface Recipient {
        void gotInput(String input);
    }

    public void setPasswordMode() {
        textFieldLayout.removeAll();
        textField = new PasswordField();
        textFieldLayout.add(textField);
        textField.focus();
    }

    private void setPlainTextMode() {
        textFieldLayout.removeAll();
        textField = new TextField();
        textFieldLayout.add(textField);
        textField.focus();
    }

}

