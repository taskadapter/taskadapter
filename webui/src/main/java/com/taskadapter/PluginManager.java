package com.taskadapter;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginManager {

    private Map<String, Descriptor> pluginDescriptors = new HashMap<>();
    private Map<String, PluginFactory<?,?>> pluginFactories = new HashMap<>();

    public PluginManager() {
        loadPlugins();
    }

    private void loadPlugins() {
        try {
            Collection<String> classNames = new PluginsFileParser().parseResource("plugins.properties");
            for (String factoryClassName : classNames) {
                @SuppressWarnings("unchecked")
				Class<PluginFactory<?,?>> factoryClass = (Class<PluginFactory<?,?>>) Class.forName(factoryClassName);
                PluginFactory<?,?> pluginFactory = factoryClass.newInstance();
                Descriptor descriptor = pluginFactory.getDescriptor();
                pluginDescriptors.put(descriptor.getId(), descriptor);
                pluginFactories.put(descriptor.getId(), pluginFactory);
            }
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    public Iterator<Descriptor> getPluginDescriptors() {
        return pluginDescriptors.values().iterator();
    }

    public Descriptor getDescriptor(String pluginId) {
        String realId = LegacyConnectorsSupport.getRealId(pluginId);
        return pluginDescriptors.get(realId);
    }

    @SuppressWarnings("unchecked")
    public <T extends ConnectorConfig, S extends ConnectorSetup> PluginFactory<T,S> getPluginFactory(String pluginId) {
        String realId = LegacyConnectorsSupport.getRealId(pluginId);
        return (PluginFactory<T, S>) pluginFactories.get(realId);
    }

}