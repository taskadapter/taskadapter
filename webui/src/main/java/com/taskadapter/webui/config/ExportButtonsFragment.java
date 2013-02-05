package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.web.data.Messages;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.Runo;

public final class ExportButtonsFragment extends HorizontalLayout {
    private Button buttonRight;
    private Button buttonLeft;
    private final Messages messages;

    public ExportButtonsFragment(Messages messages) {
        this.messages = messages;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        buttonLeft = createButton(MappingSide.LEFT);
        addComponent(buttonLeft);
        buttonRight = createButton(MappingSide.RIGHT);
        addComponent(buttonRight);
    }

    private Button createButton(MappingSide exportDirection) {
        String imageFile;

        switch (exportDirection) {
            case RIGHT:
                imageFile = "img/arrow_right.png";
                break;
            case LEFT:
                imageFile = "img/arrow_left.png";
                break;
            default:
                throw new IllegalArgumentException("Unsupported mapping direction " + exportDirection);
        }
        Button button = new Button();
        button.setIcon(new ThemeResource(imageFile));
        button.setStyleName(Runo.BUTTON_SMALL);
        button.addStyleName("exportLeftRightButton");
        button.setDescription(messages.get("export.exportButtonTooltip"));
        return button;
    }

    /**
     * @deprecated buttons must be incapsulated properly!
     */
    @Deprecated
    public Button getButtonRight() {
        return buttonRight;
    }

    /**
     * @deprecated buttons must be incapsulated properly!
     */
    @Deprecated
    public Button getButtonLeft() {
        return buttonLeft;
    }
}
