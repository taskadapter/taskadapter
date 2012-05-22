package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

public class InputDialog extends Window {

    private HorizontalLayout textFieldLayout = new HorizontalLayout();
    private AbstractTextField textField;

    public InputDialog(String caption, String question, final Recipient recipient) {
        super(caption);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        addComponent(new Label(question, Label.CONTENT_XHTML));
        addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        addComponent(textFieldLayout);

        final Window dialog = this;
        Button okButton = new Button("Ok", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                recipient.gotInput(textField.toString());
                getParent().removeWindow(dialog);
            }
        });
        okButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        okButton.addStyleName("primary");
        layout.addComponent(okButton);

        Button cancelButton = new Button("Cancel", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                getParent().removeWindow(dialog);
            }
        });
        layout.addComponent(cancelButton);
        addComponent(layout);
        setPlainTextMode();
    }

    public interface Recipient {
        public void gotInput(String input);
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

