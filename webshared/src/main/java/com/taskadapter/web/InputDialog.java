package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

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
        textLayout.addComponent(new Label(question, ContentMode.HTML));
        textLayout.addComponent(new Label("&nbsp;", ContentMode.HTML));
        textLayout.addComponent(textFieldLayout);
        view.addComponent(textLayout);

        final Window dialog = this;

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(true, false, false, false));

        Button okButton = new Button("Ok", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                recipient.gotInput(textField.getValue());
                getUI().removeWindow(dialog);
            }
        });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("v-button-default");
        layout.addComponent(okButton);

        Button cancelButton = new Button("Cancel", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getUI().removeWindow(dialog);
            }
        });
        layout.addComponent(cancelButton);
        view.addComponent(layout);
        view.setComponentAlignment(layout, Alignment.BOTTOM_CENTER);
        setPlainTextMode();
    }

    public interface Recipient {
        void gotInput(String input);
    }

    public void setPasswordMode() {
        textFieldLayout.removeAllComponents();
        textField = new PasswordField();
        textFieldLayout.addComponent(textField);
        textField.focus();
    }

    private void setPlainTextMode() {
        textFieldLayout.removeAllComponents();
        textField = new TextField();
        textFieldLayout.addComponent(textField);
        textField.focus();
    }

}

