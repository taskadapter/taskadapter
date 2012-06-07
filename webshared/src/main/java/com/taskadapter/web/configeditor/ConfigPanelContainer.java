package com.taskadapter.web.configeditor;

import com.vaadin.ui.Panel;

import java.util.HashMap;

/**
 * Store set of config editor panels and provide access by panel Type
 *
 * @author Alexander Kulik
 */
final class ConfigPanelContainer {
    private HashMap<Class<?>, Panel> map = new HashMap<Class<?>, Panel>();

    public void add(Panel panel) {
        if (panel == null)
            throw new IllegalArgumentException("Panel cannot be null");

        Class<?> type = panel.getClass();
        if (map.containsKey(type)) {
            // to prevent adding same panel twice
            throw new IllegalArgumentException("Panel with this Type was already added: " + type.getName());
        }
        map.put(type, panel);
    }

    /**
     * return Panel instance of given Class or null if not exists
     */
    public <T> T get(Class<T> type) {
        if (!map.containsKey(type)) {
            return null;
        }
        return type.cast(map.get(type));
    }

}
