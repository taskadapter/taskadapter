package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.web.ui.AbsoluteReadonlyProperty;
import com.taskadapter.web.ui.CompletableInput;
import com.taskadapter.web.ui.ConstProperty;
import com.taskadapter.web.ui.Renderer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerContainer extends GridLayout implements
        Property.ValueChangeListener {

    private static final String HOST_URL_TOOLTIP = "Host URL, including protocol prefix and port number. E.g. http://demo.site.com:3000";
    // private static final String DEFAULT_HOST_VALUE = "http://";

    private TextField descriptionField;
    private ComboBox urlCombobox;

    private final CompletableInput<ServerCompletionItem> serverInput;

    private final CompletableInput<UserCompletionItem> userInput;

    private final Renderer<ServerCompletionItem, String> serverRenderer;

    private final Renderer<UserCompletionItem, String> loginRenderer;

    public ServerContainer(List<WebServerInfo> infos, Property labelProperty,
                           final Property serverURLProperty,
                           final Property userLoginNameProperty,
                           final Property passwordProperty) {

        final ServerInfoModel model = new BasicModel(infos);
        final List<ServerCompletionItem> serverCompletionsL = getServerCompletions(model);
        final Property serverCompletions = new ConstProperty(serverCompletionsL);
        final Property serverValue = new AbsoluteReadonlyProperty(
                serverURLProperty);

        final Property userValue = new AbsoluteReadonlyProperty(userLoginNameProperty);
        final Property userCompletions = new AbsoluteReadonlyProperty(
                new AbstractProperty() {
                    {
                        if (serverURLProperty instanceof Property.ValueChangeNotifier) {
                            final Property.ValueChangeNotifier vcn = (Property.ValueChangeNotifier) serverURLProperty;
                            vcn.addListener(new ValueChangeListener() {
                                @Override
                                public void valueChange(ValueChangeEvent event) {
                                    fireValueChange();
                                }
                            });
                        }
                    }

                    @Override
                    public void setValue(Object newValue)
                            throws ReadOnlyException, ConversionException {
                        throw new ReadOnlyException();
                    }

                    @Override
                    public Object getValue() {
                        final String url = (String) serverURLProperty.getValue();
                        final List<String> items = new ArrayList<String>(model.getLogins(url));
                        Collections.sort(items);
                        final List<String> secondary = new ArrayList<String>(model.getLogins());
                        Collections.sort(secondary);
                        secondary.removeAll(items);
                        final List<UserCompletionItem> res = new ArrayList<UserCompletionItem>(items.size() + secondary.size());
                        for (String item : items) {
                            res.add(new UserCompletionItem(item, model.getPassword(url, item), true));
                        }
                        for (String item : secondary) {
                            res.add(new UserCompletionItem(item, model.getPassword(item), false));
                        }
                        return res;
                    }

                    @Override
                    public Class<?> getType() {
                        return List.class;
                    }
                });

        userInput = new CompletableInput<UserCompletionItem>() {
            @Override
            public Property value() {
                return userValue;
            }

            @Override
            public Property completions() {
                return userCompletions;
            }

            @Override
            public void freeInput(String input) {
                userLoginNameProperty.setValue(input);
            }

            @Override
            public void selection(UserCompletionItem value) {
                userLoginNameProperty.setValue(value.login);
                if (value.password != null) {
                    passwordProperty.setValue(value.password);
                }
            }
        };

        serverInput = new CompletableInput<ServerCompletionItem>() {
            @Override
            public Property value() {
                return serverValue;
            }

            @Override
            public Property completions() {
                return serverCompletions;
            }

            @Override
            public void freeInput(String input) {
                serverURLProperty.setValue(input);
            }

            @Override
            public void selection(ServerCompletionItem value) {
                serverURLProperty.setValue(value.url);
                if (value.login != null) {
                    userLoginNameProperty.setValue(value.login);
                    final String pass = model.getPassword(value.url,
                            value.login);
                    if (pass != null) {
                        passwordProperty.setValue(pass);
                    }
                }
            }
        };

        serverRenderer = new Renderer<ServerCompletionItem, String>() {
            @Override
            public String render(ServerCompletionItem item) {
                if (item.login == null) {
                    return item.url;
                }
                final String pwd = model.getPassword(item.url, item.login);
                if (pwd == null) {
                    return item.url + "[" + item.login + "]";
                }
                return item.url + "[" + item.login + "] [**********]";
            }
        };

        loginRenderer = new Renderer<UserCompletionItem, String>() {
            @Override
            public String render(UserCompletionItem item) {
                if (item.isPrimary) {
                    if (item.password != null) {
                        return item.login + " [******]";
                    }
                    return item.login;
                }
                if (item.password != null) {
                    return "---//" + item.login + " [******]";
                }
                return "---//" + item.login;
            }
        };

        buildUI(labelProperty, serverURLProperty, userLoginNameProperty,
                passwordProperty);
    }

    private static List<ServerCompletionItem> getServerCompletions(
            ServerInfoModel model) {
        final List<ServerCompletionItem> res = new ArrayList<ServerCompletionItem>();
        for (String server : model.getServers()) {
            res.add(new ServerCompletionItem(server, null));
            for (String login : model.getServers()) {
                res.add(new ServerCompletionItem(server, login));
            }
        }
        Collections.sort(res);
        return res;
    }

    private void buildUI(Property labelProperty, Property serverURLProperty,
                         Property userLoginNameProperty, Property passwordProperty) {
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
        urlCombobox.setWidth(100, UNITS_PERCENTAGE);
        urlCombobox.addListener(this);
        urlCombobox.setImmediate(true);
        addComponent(urlCombobox, 1, currentRow);
        setComponentAlignment(urlCombobox, Alignment.MIDDLE_RIGHT);
    }

    /*
     * Shows a notification when a selection is made.
     */
    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        getWindow().showNotification("Selected: " + event.getProperty());
    }

    // private void cleanup() {
    // if (getHostString().endsWith("/")) {
    // hostURLText.setValue(getHostString().substring(0,
    // getHostString().length() - 1));
    // }
    // }
    //
    // private void checkProtocol() {
    // if (!getHostString().startsWith("http")) {
    // hostURLText.setValue(DEFAULT_HOST_VALUE + hostURLText.getValue());
    // }
    // }

    String getHostString() {
        return (String) urlCombobox.getValue();
    }

}
