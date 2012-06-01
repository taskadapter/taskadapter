package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.vaadin.ui.Panel;

import java.util.HashMap;

/**
 * Store set of config editor panels and provide access by panel Type
 *
 * @author Alexander Kulik
 */
public class ConfigPanelContainer {
    private HashMap<Class, Panel> map = new HashMap<Class, Panel>();

    public void add(Panel panel) {
        if (panel == null)
            throw new IllegalArgumentException("Panel cannot be null");

        Class type = panel.getClass();
        if (map.containsKey(type)) {
            // to prevent adding same panel twice
            throw new IllegalArgumentException("Panel with this Type was already added: " + type.getName());
        }
        map.put(type, panel);
    }

    public boolean contains(Class type) {
        return map.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    /**
     * return Panel instance of given Class or null if not exists
     */
    public <T> T get(Class<T> type) {
        if (!map.containsKey(type)) {
            return null;
        }
        return (T) map.get(type);
    }

    public void setPanelsDataToConfig(ConnectorConfig config) {
        for (Panel panel : map.values()) {
            if (panel instanceof ConfigPanel) {
                ConfigPanel configPanel = (ConfigPanel) panel;
                configPanel.setDataToConfig(config);
            }
        }
    }

    public void initPanelsDataByConfig(ConnectorConfig config) {
        for (Panel panel : map.values()) {
            if (panel instanceof ConfigPanel) {
                ConfigPanel configPanel = (ConfigPanel) panel;
                configPanel.initDataByConfig(config);
            }
        }
    }

}
