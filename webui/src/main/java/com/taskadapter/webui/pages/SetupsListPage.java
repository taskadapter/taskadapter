package com.taskadapter.webui.pages;

import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.web.PopupDialog;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.web.event.EventTracker;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.Sizes;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import scala.collection.JavaConverters;

import java.util.Comparator;

import static com.taskadapter.web.event.EventCategory.SetupCategory;

@Route(value = Navigator.SETUPS_LIST, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class SetupsListPage extends BasePage {
    private final ConfigOperations configOps;
    private VerticalLayout elementsComponent;

    public SetupsListPage() {
        configOps = SessionController.buildConfigOperations();
        buildUi();
        refresh();
    }

    private void buildUi() {
        var introLabel = new Label(Page.message("setupsListPage.intro"));
        introLabel.setWidth(null);
        var addButton = new Button(Page.message("setupsListPage.addButton"),
                e -> Navigator.newSetup());
        var introRow = new HorizontalLayout(introLabel, addButton);

        elementsComponent = new VerticalLayout();

        add(LayoutsUtil.centered(Sizes.mainWidth, introRow, elementsComponent));
    }

    private void refresh() {
        elementsComponent.removeAll();
        addElements();
    }

    private void addElements() {
        var setups = configOps.getConnectorSetups();
        setups
                .stream()
                .sorted(Comparator.comparing(ConnectorSetup::getConnectorId))
                .forEach(setup -> {
                    var setupId = new SetupId(setup.getId());
                    var editButton = new Button(Page.message("setupsListPage.editButton"),
                            e -> {
                                getUI().get().navigate("edit-setup/" + setup.getId());
                            });
                    var connectorIdLabel = new Label(setup.getConnectorId());
                    connectorIdLabel.setWidth(null);
                    connectorIdLabel.addClassName("myBoldLabel");

                    var connectorIdLayout = new HorizontalLayout(connectorIdLabel);
                    connectorIdLayout.setWidth("200px");
                    connectorIdLayout.setHeight("100%");

                    var setupLabel = new Label(setup.getLabel());
                    setupLabel.setWidth(null);
                    var usedByConfigs = configOps.getConfigIdsUsingThisSetup(setupId);
                    var usedByLabel = new Label(Page.message("setupsListPage.usedByConfigs", usedByConfigs.size() + ""));

                    var descriptionLayout = new VerticalLayout(setupLabel, usedByLabel);
                    descriptionLayout.setWidth("450px");
                    descriptionLayout.setHeight("80px");

                    var editLayout = new VerticalLayout(editButton);
                    editLayout.setWidth("100px");
                    editLayout.setHeight("100%");

                    var deleteButton = new Button(Page.message("setupsListPage.deleteButton"),
                            e -> showDeleteDialog(setupId));

                    deleteButton.setEnabled(usedByConfigs.isEmpty());
                    var deleteLayout = new VerticalLayout(deleteButton);
                    deleteLayout.setWidth("100px");
                    deleteLayout.setHeight("100%");

                    var row = new HorizontalLayout(connectorIdLayout,
                            descriptionLayout,
                            editLayout,
                            deleteLayout
                    );

                    elementsComponent.add(row);
                });
    }

    private void showDeleteDialog(SetupId setupId) {
        PopupDialog.confirm(Page.message("setupsListPage.confirmDelete.question"),
                () -> {
                    configOps.deleteConnectorSetup(setupId);
                    EventTracker.trackEvent(SetupCategory, "deleted", "");
                    refresh();
                });
    }
}
