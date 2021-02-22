package com.taskadapter.webui.pages;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;

public class ModalWindow {
    public static Dialog showDialog(Component component) {
        var dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.add(component);
        dialog.open();
        return dialog;
    }
}
