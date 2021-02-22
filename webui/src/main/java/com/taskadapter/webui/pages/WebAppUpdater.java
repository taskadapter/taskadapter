package com.taskadapter.webui.pages;

import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

public class WebAppUpdater {

    private static final String TASKADAPTER_DOWNLOAD_URL = "http://www.taskadapter.com/download";

    public static Component createDownloadLink() {
        return new Button(Page.message("supportPage.openDownloadPage"),
                event -> UI.getCurrent().getPage()
                        .executeJs("window.open('" + TASKADAPTER_DOWNLOAD_URL + "');")
        );
    }
}
