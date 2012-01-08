package com.taskadapter.webui;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Alexey Skorokhodov
 */
public class MenuLinkBuilder {
    private PageManager pageManager;

    public MenuLinkBuilder(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public Button render(String caption, final Page page) {
        return render(caption, page, null);
    }

    public Button render(String caption, final Page page, Resource icon) {
        Button button = new Button(caption);
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.setIcon(icon);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                pageManager.show(page);
            }
        });

        return button;
    }
}
