package com.taskadapter.web;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

public class InputDialog extends Window {

    private HorizontalLayout layout = new HorizontalLayout();
    private TextField textField;
    private Recipient recipient;

    // TODO this is inconsistent with MessageDialog class, where we don't keep Parent Window inside
    // - the calling code adds the subwindow to main window instead.
    // this sample code was taken from Vaadin.com
    public InputDialog(final Window parent, String caption, String question, final Recipient recipient) {
        super(caption);
        this.recipient = recipient;

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        layout.setSpacing(true);

        addComponent(new Label(question, Label.CONTENT_XHTML));
        addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        textField = new TextField();
        addComponent(textField);
        textField.focus();

        final Window dialog = this;
        addComponent(new Button("Ok", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                recipient.gotInput(textField.toString());
                parent.removeWindow(dialog);
            }
        }));
        addComponent(layout);
        parent.addWindow(this);
    }

    public interface Recipient {
        public void gotInput(String input);
    }
}

