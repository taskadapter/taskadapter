package com.taskadapter.webui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationUtil {
    public static void showError(String text) {
        var notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
    }
}
