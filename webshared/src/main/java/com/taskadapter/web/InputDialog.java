package com.taskadapter.web;

import com.taskadapter.vaadin14shim.Label;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

/**
 * popup dialog with "ok" and "cancel" buttons.
 * <p>
 * sample usage:
 * <pre>
 *       InputDialog.showSecret(
 *                      message("users.changePassword", userLoginName),
 *                      message("users.newPassword"),
 *        (newPassword) => {
 *          // save here
 *        });
 *
 * </pre>
 */
public class InputDialog extends Dialog {

    public InputDialog(String caption, SavableComponent component) {
        Html captionLabel = new Html("<b>" + caption + "</b>");

        setModal(true);
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        Button okButton = new Button("Ok", event -> {
            if (component.save()) {
                close();
            }
        });
        okButton.addClickShortcut(Key.ENTER);

        Button cancelButton = new Button("Cancel",
                event -> close());

        VerticalLayout layout = new VerticalLayout(
                captionLabel,
                component.getComponent(),
                new HorizontalLayout(okButton, cancelButton));
        layout.setSpacing(true);

        add(layout);
    }

    public static void show(String caption, String question, Recipient recipient) {
        TextField textField = new TextField();
        FormLayout form = new FormLayout();
        form.add(
                new Html(question),
                textField);
        textField.focus();

        new InputDialog(caption,
                new DefaultSavableComponent(form, () -> {
                    recipient.gotInput(textField.getValue());
                    return true;
                })).open();
    }

    /**
     * show a popup dialog with a single secret (password-style) field.
     */
    public static void showSecret(String caption, String question, Recipient recipient) {
        PasswordField field = new PasswordField();
        FormLayout form = new FormLayout();
        form.add(
                new Label(question),
                field);
        field.focus();

        new InputDialog(caption,
                new DefaultSavableComponent(form, () -> {
                    recipient.gotInput(field.getValue());
                    return true;
                })).open();
    }

    public interface Recipient {
        void gotInput(String input);
    }
}

