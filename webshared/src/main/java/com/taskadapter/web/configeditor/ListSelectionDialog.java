package com.taskadapter.web.configeditor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.listbox.ListBox;

import java.util.Collection;

public class ListSelectionDialog extends Dialog {

    public ListSelectionDialog(String windowTitle, Collection<String> items, EditorUtil.ValueListener valueListener) {

        setCloseOnEsc(true);
        setWidth("350px");

        add(EditorUtil.createCaption(windowTitle));

        var listSelect = new ListBox<String>();

        var closeButton = new Button("Select", event -> {
            valueListener.setValue((String) listSelect.getValue());
            close();
        });

        listSelect.setItems(items);
        listSelect.setWidth("300px");
        listSelect.addValueChangeListener(event -> closeButton.setEnabled(true));
        add(listSelect);

        closeButton.setEnabled(false);
        add(closeButton);
    }
}
