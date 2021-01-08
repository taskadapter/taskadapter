package com.taskadapter.config;

import com.taskadapter.web.uiapi.SetupId;

import java.util.Random;

public final class NewConfigParser {
    private static final String ID_PREFIX = "ta.id=";
    private static final String NAME_PREFIX = "ta.name=";
    private static final String CONN1_ID_PREFIX = "ta.connector1.id=";
    private static final String CONN1_SAVED_SETUP_ID = "ta.connector1.savedSetupId=";
    private static final String CONN1_DATA_PREFIX = "ta.connector1.data=";
    private static final String CONN2_ID_PREFIX = "ta.connector2.id=";
    private static final String CONN2_SAVED_SETUP_ID = "ta.connector2.savedSetupId=";
    private static final String CONN2_DATA_PREFIX = "ta.connector2.data=";
    private static final String MAPPINGS_PREFIX = "mappings=";

    public static StoredExportConfig parse(String fileContents) {
        final String lines[] = fileContents.split("\\r?\\n");

        String idString = findString(ID_PREFIX, lines);
        int configId = Integer.parseInt(idString);
        final String name = findString(NAME_PREFIX, lines);
        final String connector1ID = findString(CONN1_ID_PREFIX, lines);
        final String connector1SavedSetupIdString = findString(CONN1_SAVED_SETUP_ID, lines);
        final String connector1DataString = findString(CONN1_DATA_PREFIX, lines);

        final String connector2ID = findString(CONN2_ID_PREFIX, lines);
        final String connector2SavedSetupIdString = findString(CONN2_SAVED_SETUP_ID, lines);
        final String connector2DataString = findString(CONN2_DATA_PREFIX, lines);

        final String mappings = findString(MAPPINGS_PREFIX, lines);

        return new StoredExportConfig(configId, name,
                new StoredConnectorConfig(connector1ID, SetupId.apply(connector1SavedSetupIdString), connector1DataString),
                new StoredConnectorConfig(connector2ID, SetupId.apply(connector2SavedSetupIdString), connector2DataString),
                mappings);
    }

    /**
     * legacy configs do not have "ta.id" field and thus have no numeric id in them. they used full file name
     *  as "id" until November 2020. Yay, 2020 is almost over now! I hope you all survived it.
     */
    public static StoredExportConfig parseLegacyConfig(int id, String fileContents) {
        final String lines[] = fileContents.split("\\r?\\n");

        String idString = findString(ID_PREFIX, lines);
        int configId = idString == null ? new Random().nextInt() : Integer.parseInt(idString);
        final String name = findString(NAME_PREFIX, lines);
        final String connector1ID = findString(CONN1_ID_PREFIX, lines);
        final String connector1SavedSetupIdString = findString(CONN1_SAVED_SETUP_ID, lines);
        final String connector1DataString = findString(CONN1_DATA_PREFIX, lines);

        final String connector2ID = findString(CONN2_ID_PREFIX, lines);
        final String connector2SavedSetupIdString = findString(CONN2_SAVED_SETUP_ID, lines);
        final String connector2DataString = findString(CONN2_DATA_PREFIX, lines);

        final String mappings = findString(MAPPINGS_PREFIX, lines);

        return new StoredExportConfig(configId, name,
                new StoredConnectorConfig(connector1ID, SetupId.apply(connector1SavedSetupIdString), connector1DataString),
                new StoredConnectorConfig(connector2ID, SetupId.apply(connector2SavedSetupIdString), connector2DataString),
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

    static String toFileContent(int configId,
                                String configName,
                                String connector1Id, SetupId connector1SavedSetupId, String connector1Data,
                                String connector2Id, SetupId connector2SavedSetupId, String connector2Data,
                                String mappings) {
        String result = ID_PREFIX + configId + "\n" +
                NAME_PREFIX + configName + "\n" +
                CONN1_ID_PREFIX + connector1Id + "\n" +
                CONN1_SAVED_SETUP_ID + connector1SavedSetupId.id() + "\n" +
                CONN1_DATA_PREFIX + connector1Data + "\n" +
                CONN2_ID_PREFIX + connector2Id + "\n" +
                CONN2_SAVED_SETUP_ID + connector2SavedSetupId.id() + "\n" +
                CONN2_DATA_PREFIX + connector2Data + "\n" +
                MAPPINGS_PREFIX + mappings + "\n";
        return result;
    }
}
