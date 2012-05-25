package com.taskadapter.webui;

import com.vaadin.ui.Button;

// TODO Review PageUtil and EditorUtil.
// the reason why I added PageUtil is because EditorUtil cannot access Navigator
public class PageUtil {
    // TODO maybe move this to Services or Navigator to eliminate static method calls
    public static Button createButton(final Navigator navigator, String buttonLabel, final String pageId) {
        Button button = new Button(buttonLabel);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.show(pageId);
            }
        });
        return button;
    }
}
