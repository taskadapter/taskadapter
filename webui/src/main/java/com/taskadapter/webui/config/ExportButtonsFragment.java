package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.MappingSide;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

class ExportButtonsFragment extends VerticalLayout {
    private Button buttonRight;
    private Button buttonLeft;

    ExportButtonsFragment() {
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        buttonRight = createButton(MappingSide.RIGHT);
        addComponent(buttonRight);
        buttonLeft = createButton(MappingSide.LEFT);
        addComponent(buttonLeft);
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
        return button;
    }

    Button getButtonRight() {
        return buttonRight;
    }

    Button getButtonLeft() {
        return buttonLeft;
    }
}
