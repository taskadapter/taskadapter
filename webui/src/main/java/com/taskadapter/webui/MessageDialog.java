package com.taskadapter.webui;

import com.vaadin.ui.*;

public class MessageDialog extends Window implements Button.ClickListener {

    Callback callback;
    Button yes = new Button("Yes", this);
    Button no = new Button("No", this);

    public MessageDialog(String caption, String question, Callback callback) {
        super(caption);

        setModal(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(yes);
        hl.addComponent(no);
        addComponent(hl);
        hl.setComponentAlignment(yes, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(no, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void buttonClick(Button.ClickEvent clickEvent) {
        if (getParent() != null) {
            ((Window) getParent()).removeWindow(this);
        }
        callback.onDialogResult(clickEvent.getSource() == yes);
    }

    public interface Callback {
        public void onDialogResult(boolean resultIsYes);
    }
}

