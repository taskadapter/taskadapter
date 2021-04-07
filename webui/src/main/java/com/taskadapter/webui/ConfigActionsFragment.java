package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.web.PopupDialog;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.webui.pages.Navigator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains buttons with various config actions (Clone, Delete, etc). Shown on Config Summary page.
 */
public class ConfigActionsFragment extends HorizontalLayout {
    private final ConfigId configId;

    private static final Logger log = LoggerFactory.getLogger(ConfigActionsFragment.class);
    private final ConfigOperations configOps;

    /**
     * @param configId identity of the config to perform operations on.
     */
    public ConfigActionsFragment(ConfigId configId) {
        this.configId = configId;
        configOps = SessionController.buildConfigOperations();

        add(new Button(Page.message("configsPage.actionClone"), event -> showConfirmClonePage()));
        add(new Button(Page.message("configsPage.actionDelete"), event -> showDeleteConfigDialog()));
    }

    private void showDeleteConfigDialog() {
        PopupDialog.confirm(Page.message("configsPage.actionDelete.confirmText"),
                () -> {
                    configOps.deleteConfig(configId);
                    Navigator.configsList();
                }
        );
    }

    private void showConfirmClonePage() {
        PopupDialog.confirm(Page.message("configsPage.actionClone.confirmText"),
                () -> {
                    try {
                        configOps.cloneConfig(configId);
                        Navigator.configsList();
                        Notification.show(Page.message("configsPage.actionClone.success"))
                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    } catch (StorageException e) {
                        var message = Page.message("configsPage.actionClone.error", e.getMessage());
                        log.error(message, e);
                        Notification.show(message)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }
        );
    }
}
