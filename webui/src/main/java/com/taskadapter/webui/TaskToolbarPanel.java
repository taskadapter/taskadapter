package com.taskadapter.webui;

import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.TAFile;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.BaseTheme;

import java.util.Arrays;

public class TaskToolbarPanel extends HorizontalLayout {
    private Button cloneButton = new Button("Clone config");
    private final PageManager pageManager;
    private ConfigStorage storage;
    private final TAFile file;
    private ConfigureTaskPage configureTaskPage;

    // TODO refactor this huge list of parameters!
    public TaskToolbarPanel(PageManager pageManager, ConfigStorage storage, TAFile file, EditorManager editorManager, SettingsManager settingsManager) {
        this.pageManager = pageManager;
        this.storage = storage;
        this.file = file;

        configureTaskPage = new ConfigureTaskPage(file, editorManager, storage, settingsManager);
        pageManager.registerPage(PageManager.CONFIGURE_TASK_PAGE_ID_PREFFIX + file.getName(), configureTaskPage);

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
                final String YES = "Yes";
                String NO = "No";

                getWindow().addWindow(new MessageDialog("Confirm Clone", "Clone the selected config?", Arrays.asList(YES, NO),
                        new MessageDialog.Callback() {
                            public void onDialogResult(String answer) {
                                if (answer.equals(YES)) {
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
        pageManager.show(configureTaskPage);
    }
}
