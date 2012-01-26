package com.taskadapter.webui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class HomePage extends Page {
    public HomePage() {
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.addComponent(new Label("Home page"));


/*
        // For CustomFieldsTable testing

        List<CustomField> customFields = Arrays.asList(
                new CustomField("0", "Very very very very long value"),
                new CustomField("1", "Not very long value"),
                new CustomField("100", "Not very long value"),
                new CustomField("9999", "Very very very very long value"),
                new CustomField("15", "just value"),
                new CustomField("5", "just value"),
                new CustomField("2", "Short value")
        );
        final CustomFieldsTable table = new CustomFieldsTable(customFields);

        final Label label = new Label();
        label.setContentMode(Label.CONTENT_XHTML);

        Button btn = new Button("test");
        btn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick (Button.ClickEvent event) {
                List<CustomField> newCustomFields = table.getCustomFields();
                if(!newCustomFields.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for(CustomField customField : newCustomFields) {
                        sb.append("id=").append(customField.getId())
                                .append(" value=").append(customField.getValue())
                                .append("<br/>");
                    }
                    label.setValue(sb.toString());
                } else {
                    label.setValue("no custom fields");
                }
            }
        });

        layout.addComponent(table);
        layout.addComponent(btn);
        layout.addComponent(label);
*/

        setCompositionRoot(layout);
    }

    @Override
    public String getNavigationPanelTitle() {
        return "";
    }
}
