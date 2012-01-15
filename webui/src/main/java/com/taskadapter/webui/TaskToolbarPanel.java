package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAFile;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class TaskToolbarPanel extends HorizontalLayout {
    private Button cloneButton = new Button("Clone config");
    private final PageManager pageManager;
    private ConfigStorage storage;
    private final TAFile file;
    private EditorManager editorManager;
    private SettingsManager settingsManager;

    // TODO refactor this huge list of parameters!
    public TaskToolbarPanel(PageManager pageManager, ConfigStorage storage, TAFile file,  EditorManager editorManager, SettingsManager settingsManager) {
        this.pageManager = pageManager;
        this.storage = storage;
        this.file = file;
        this.editorManager = editorManager;
        this.settingsManager = settingsManager;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        Button configureButton = new Button("Configure");
        configureButton.setStyleName(BaseTheme.BUTTON_LINK);
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showConfigurePage();
            }
        });
        addComponent(configureButton);

        cloneButton.setStyleName(BaseTheme.BUTTON_LINK);
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().addWindow(new MessageDialog("Confirm Clone", "Clone the selected config?",
                        new MessageDialog.Callback() {
                            public void onDialogResult(boolean yes) {
                                if (yes) {
                                    storage.cloneConfig(file);
                                    pageManager.show(PageManager.TASKS);
                                }
                            }
                        }
                ));
            }
        });
        addComponent(cloneButton);

        Button deleteButton = new Button("Delete config");
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showDeletePage();
            }
        });
        addComponent(deleteButton);

    }

    private void showDeletePage() {
        DeletePage page = new DeletePage(pageManager, storage, file);
        pageManager.show(page);
    }

    private void showConfigurePage() {
        ConfigureTaskPage page = new ConfigureTaskPage(file, editorManager, storage, settingsManager);
        pageManager.show(page);
    }
}
