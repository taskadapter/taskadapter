package com.taskadapter.config;

/**
 * New configuration parser.
 * 
 */
public final class NewConfigParser {
    private static final String NAME_PREFIX = "ta.name=";
    private static final String CONN1_ID_PREFIX = "ta.connector1.id=";
    private static final String CONN1_SAVED_SETUP_ID = "ta.connector1.savedSetupId=";
    private static final String CONN1_DATA_PREFIX = "ta.connector1.data=";
    private static final String CONN2_ID_PREFIX = "ta.connector2.id=";
    private static final String CONN2_SAVED_SETUP_ID = "ta.connector2.savedSetupId=";
    private static final String CONN2_DATA_PREFIX = "ta.connector2.data=";
    private static final String MAPPINGS_PREFIX = "mappings=";

    public static StoredExportConfig parse(String id, String fileContents) {
        final String lines[] = fileContents.split("\\r?\\n");

        final String name = findString(NAME_PREFIX, lines);
        final String connector1ID = findString(CONN1_ID_PREFIX, lines);
        final String connector1SavedSetupIdString = findString(CONN1_SAVED_SETUP_ID, lines);
        final String connector1DataString = findString(CONN1_DATA_PREFIX, lines);

        final String connector2ID = findString(CONN2_ID_PREFIX, lines);
        final String connector2SavedSetupIdString = findString(CONN2_SAVED_SETUP_ID, lines);
        final String connector2DataString = findString(CONN2_DATA_PREFIX, lines);

        final String mappings = findString(MAPPINGS_PREFIX, lines);

        return new StoredExportConfig(id, name,
                new StoredConnectorConfig(connector1ID, connector1SavedSetupIdString, connector1DataString),
                new StoredConnectorConfig(connector2ID, connector2SavedSetupIdString, connector2DataString),
                mappings);
    }

    private static String findString(String prefix, String[] strings) {
        for (String string : strings) {
            if (string.startsWith(prefix)) {
                return string.substring(prefix.length());
            }
        }
        return null;
    }

    static String toFileContent(String configName,
                                String connector1Id, String connector1SavedSetupId, String connector1Data,
                                String connector2Id, String connector2SavedSetupId, String connector2Data,
                                String mappings) {
        final StringBuilder result = new StringBuilder();
        result.append(NAME_PREFIX).append(configName).append("\n");
        result.append(CONN1_ID_PREFIX).append(connector1Id).append("\n");
        result.append(CONN1_SAVED_SETUP_ID).append(connector1SavedSetupId).append("\n");
        result.append(CONN1_DATA_PREFIX).append(connector1Data).append("\n");
        result.append(CONN2_ID_PREFIX).append(connector2Id).append("\n");
        result.append(CONN2_SAVED_SETUP_ID).append(connector2SavedSetupId).append("\n");
        result.append(CONN2_DATA_PREFIX).append(connector2Data).append("\n");
        result.append(MAPPINGS_PREFIX).append(mappings).append("\n");
        return result.toString();
    }
}
