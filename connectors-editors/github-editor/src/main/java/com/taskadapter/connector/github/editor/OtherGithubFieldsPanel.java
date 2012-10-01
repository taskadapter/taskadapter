package com.taskadapter.connector.github.editor;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Author: Alexander Kulik
 * Date: 21.05.12 23:27
 */
public class OtherGithubFieldsPanel extends Panel {
    private static final String DEFAULT_PANEL_CAPTION = "Additional Info";
    private final VerticalLayout verticalLayout = new VerticalLayout();

    public OtherGithubFieldsPanel(GithubEditor githubEditor) {
        buildUI();
    }

    private void buildUI() {
        setCaption(DEFAULT_PANEL_CAPTION);
        addComponent(verticalLayout);
        verticalLayout.setSpacing(true);

        final CheckBox checkBox = new CheckBox("Save issue relations (follows/precedes)"); //TODO check because unused
        checkBox.setEnabled(false);
        verticalLayout.addComponent(checkBox);
    }
}
