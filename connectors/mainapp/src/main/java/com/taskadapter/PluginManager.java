package com.taskadapter;

import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginManager {

    private Map<String, Descriptor> pluginDescriptors = new HashMap<String, Descriptor>();
    private Map<String, PluginFactory> pluginFactories = new HashMap<String, PluginFactory>();

    public PluginManager() {
        loadPlugins();
    }

    private void loadPlugins() {
        try {
            Collection<String> classNames = new PluginsFileParser().parseResource("plugins.txt");
            for (String factoryClassName : classNames) {
                Class<PluginFactory> factoryClass = (Class<PluginFactory>) Class.forName(factoryClassName);
                PluginFactory pluginFactory = factoryClass.newInstance();
                Descriptor descriptor = pluginFactory.getDescriptor();
                pluginDescriptors.put(descriptor.getID(), descriptor);
                pluginFactories.put(descriptor.getID(), pluginFactory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Iterator<Descriptor> getPluginDescriptors() {
        return pluginDescriptors.values().iterator();
    }

    public Descriptor getDescriptor(String pluginId) {
        String realId = LegacyConnectorsSupport.getRealId(pluginId);
        return pluginDescriptors.get(realId);
    }

    public PluginFactory getPluginFactory(String pluginId) {
        String realId = LegacyConnectorsSupport.getRealId(pluginId);
        return pluginFactories.get(realId);
    }

}