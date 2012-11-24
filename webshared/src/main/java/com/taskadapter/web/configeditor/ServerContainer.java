package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.util.List;

public class ServerContainer extends GridLayout implements Property.ValueChangeListener {
    private static final String HOST_URL_TOOLTIP = "Host URL, including protocol prefix and port number. E.g. http://demo.site.com:3000";
    private final ServerInfoCache serverInfoCache;
//    private static final String DEFAULT_HOST_VALUE = "http://";

    private TextField descriptionField;
    //    private TextField hostURLText;
    private ComboBox urlCombobox;

    public ServerContainer(ServerInfoCache serverInfoCache, Property labelProperty, Property serverURLProperty, Property userLoginNameProperty,
                           Property passwordProperty) {
        this.serverInfoCache = serverInfoCache;
        buildUI(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);
    }

    private void buildUI(Property labelProperty, Property serverURLProperty, Property userLoginNameProperty, Property passwordProperty) {
        setColumns(2);
        setRows(4);
        setMargin(true);
        setSpacing(true);

        int currentRow = 0;
        Label descriptionLabel = new Label("Description:");
        addComponent(descriptionLabel, 0, currentRow);
        setComponentAlignment(descriptionLabel, Alignment.MIDDLE_LEFT);
        descriptionField = new TextField();
        descriptionField.addStyleName("server-panel-textfield");
        descriptionField.setPropertyDataSource(labelProperty);
        addComponent(descriptionField, 1, currentRow);

        currentRow++;

        Label urlLabel = new Label("Server URL:");
        addComponent(urlLabel, 0, currentRow);
        setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

//        hostURLText = new TextField();
//        hostURLText.setDescription(HOST_URL_TOOLTIP);
//        hostURLText.addListener(new FieldEvents.BlurListener() {
//            @Override
//            public void blur(FieldEvents.BlurEvent event) {
//                //TODO refactor these methods (common in ServerPanel and RedmineServerPanel)
//                checkProtocol();
//                cleanup();
//            }
//        });
//        hostURLText.addStyleName("server-panel-textfield");
//        hostURLText.setPropertyDataSource(serverURLProperty);
//        addComponent(hostURLText, 1, currentRow);
//        setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        addUrlCombobox(currentRow);

        currentRow++;

        Label loginLabel = new Label("Login:");
        addComponent(loginLabel, 0, currentRow);
        setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        TextField login = new TextField();
        login.addStyleName("server-panel-textfield");
        login.setPropertyDataSource(userLoginNameProperty);
        addComponent(login, 1, currentRow);
        setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label pswdLabel = new Label("Password:");
        addComponent(pswdLabel, 0, currentRow);
        setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        PasswordField password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(passwordProperty);
        addComponent(password, 1, currentRow);
        setComponentAlignment(password, Alignment.MIDDLE_RIGHT);
    }

    private void addUrlCombobox(int currentRow) {
        urlCombobox = new ComboBox();
        urlCombobox.setDescription(HOST_URL_TOOLTIP);
        urlCombobox.setNullSelectionAllowed(false);
        urlCombobox.setTextInputAllowed(true);
        urlCombobox.setNewItemsAllowed(true);
        urlCombobox.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        // defines width
        urlCombobox.setWidth(100, UNITS_PERCENTAGE);
        urlCombobox.addListener(this);
//        urlCombobox.setNewItemHandler(this);
        urlCombobox.setImmediate(true);
        addComponent(urlCombobox, 1, currentRow);
        setComponentAlignment(urlCombobox, Alignment.MIDDLE_RIGHT);
        loadPreviousServerURLsForThisServerType();
    }

    private void loadPreviousServerURLsForThisServerType() {
        List<WebServerInfo> values = serverInfoCache.getValues();
        for (WebServerInfo value : values) {
            urlCombobox.addItem(value);
        }
    }

//    private Boolean lastAdded = false;

    /*
     * Shows a notification when a selection is made.
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
//        if (!lastAdded) {
        getWindow().showNotification(
                "Selected: " + event.getProperty());
//        }
//        lastAdded = false;
    }

//    @Override
//    public void addNewItem(String newItemCaption) {
//        if (!urlCombobox.containsId(newItemCaption)) {
//            getWindow().showNotification("Added: " + newItemCaption);
//            lastAdded = true;
//            urlCombobox.addItem(newItemCaption);
//            urlCombobox.setValue(newItemCaption);
//        }
//    }
//    private void cleanup() {
//        if (getHostString().endsWith("/")) {
//            hostURLText.setValue(getHostString().substring(0, getHostString().length() - 1));
//        }
//    }
//
//    private void checkProtocol() {
//        if (!getHostString().startsWith("http")) {
//            hostURLText.setValue(DEFAULT_HOST_VALUE + hostURLText.getValue());
//        }
//    }

//    TextField getServerURLField() {
//        return urlCombobox.getValue();
//    }

    String getHostString() {
        return (String) urlCombobox.getValue();
    }

}
