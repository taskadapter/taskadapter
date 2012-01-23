package com.taskadapter.web.configeditor;

import com.vaadin.ui.Button;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Window;

import java.util.Collection;

public class ListSelectionDialog extends Window {
    private String title;
    private Collection<String> items;
    private EditorUtil.ValueListener valueListener;

    public ListSelectionDialog(String title, Collection<String> items, EditorUtil.ValueListener valueListener) {
        this.title = title;
        this.items = items;
        this.valueListener = valueListener;
        buildUI();
    }

    private void buildUI() {
        final ListSelect listSelect = new ListSelect(title, items);
        listSelect.setNullSelectionAllowed(false);
        addComponent(listSelect);

        Button closeButton = new Button("Select");
        closeButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                valueListener.setValue((String) listSelect.getValue());
                close();
            }
        });
        addComponent(closeButton);
    }
}
