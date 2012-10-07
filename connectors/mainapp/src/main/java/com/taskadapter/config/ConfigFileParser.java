package com.taskadapter.config;

import com.google.gson.JsonParser;
import com.taskadapter.PluginManager;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.definition.PluginFactory;

public class ConfigFileParser {
    private static final String LINE0_PREFIX = "ta.name=";
    private static final String LINE1_PREFIX = "ta.connector1.id=";
    private static final String LINE2_PREFIX = "ta.connector1.data=";
    private static final String LINE3_PREFIX = "ta.connector2.id=";
    private static final String LINE4_PREFIX = "ta.connector2.data=";
    private static final String LINE_MAPPINGS_PREFIX = "mappings=";

    private PluginManager pluginManager;

    public ConfigFileParser(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public TAFile parse(String fileContents) {
        String lines[] = fileContents.split("\\r?\\n");

        String name = lines[0].substring(LINE0_PREFIX.length());
        String connector1ID = lines[1].substring(LINE1_PREFIX.length());
        String connector1DataString = lines[2].substring(LINE2_PREFIX.length());
        ConnectorConfig config1 = createConfig(connector1ID, connector1DataString);

        String connector2ID = lines[3].substring(LINE3_PREFIX.length());
        String connector2DataString = lines[4].substring(LINE4_PREFIX.length());
        ConnectorConfig config2 = createConfig(connector2ID, connector2DataString);

        ConnectorDataHolder desc1 = new ConnectorDataHolder(connector1ID,
                config1);
        ConnectorDataHolder desc2 = new ConnectorDataHolder(connector2ID,
                config2);

        TAFile taFile = new TAFile(name, desc1, desc2);

        // safe migration for old configs without a top-level "mappings" element.
        if (lines.length > 5) {
            String mappingsString = lines[5].substring(LINE_MAPPINGS_PREFIX.length());
            NewMappings mappings = ConfigUtils.createDefaultGson().fromJson(mappingsString, NewMappings.class);
            taFile.setMappings(mappings);
        }

        return taFile;
    }

    private ConnectorConfig createConfig(String pluginId, String dataString) {
        final PluginFactory<?> factory = pluginManager.getPluginFactory(pluginId);
        if (factory == null) {
            throw new RuntimeException("Connector with ID " + pluginId + " is not found.");
        }
        return factory.readConfig(new JsonParser().parse(dataString));
    }

    @SuppressWarnings("unchecked")
    public String convertToJSonString(TAFile file) {
        String line0 = LINE0_PREFIX + file.getConfigLabel();

        String plugin1Type = file.getConnectorDataHolder1().getType();
        String line1 = LINE1_PREFIX + plugin1Type;
        ConnectorConfig data1 = file.getConnectorDataHolder1().getData();
        String line2 = LINE2_PREFIX + pluginManager.getPluginFactory(plugin1Type).writeConfig(data1);

        String pluginType2 = file.getConnectorDataHolder2().getType();
        String line3 = LINE3_PREFIX + pluginType2;
        ConnectorConfig data2 = file.getConnectorDataHolder2().getData();
        String line4 = LINE4_PREFIX + pluginManager.getPluginFactory(pluginType2).writeConfig(data2);

        String line5 = LINE_MAPPINGS_PREFIX + ConfigUtils.createDefaultGson().toJson(file.getMappings());

        return line0 + "\n" + line1 + "\n" + line2 + "\n" + line3
                + "\n" + line4 + "\n" + line5;
    }

}
