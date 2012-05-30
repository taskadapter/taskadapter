package com.taskadapter.web.configeditor;

import com.vaadin.ui.Panel;

import java.util.HashMap;

/**
 * Store set of config editor panels and provide access by panel Type
 *
 * @Author: Alexander Kulik
 * @Date: 30.05.12 19:25
 */
public class ConfigPanelContainer {
    private HashMap<Class, Panel> map = new HashMap<Class, Panel>();
    
    public void add(Panel panel) {
        if (panel == null)
            throw new IllegalArgumentException("Panel cannot be null");

        Class type = panel.getClass();
        if (map.containsKey(type)) {
            // to prevent adding same panel twice
            throw new IllegalArgumentException("Panel with this Type was already added: "+ type.getName());
        }
        map.put(type, panel);
    }
    
    public boolean contains(Class type) {
        return map.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    /**
     * return Panel instance of given Class
     */
    public <T> T get(Class<T> type) {
        if (!map.containsKey(type)) {
            throw new IllegalArgumentException("Panel with given Type not found in container: " + type.getName());
        }
        return (T)map.get(type);
    }
}
