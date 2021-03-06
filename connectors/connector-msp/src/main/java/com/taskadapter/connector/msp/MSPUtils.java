package com.taskadapter.connector.msp;

import com.taskadapter.connector.msp.write.MSPDefaultFields;
import com.taskadapter.connector.msp.write.RealWriter;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.TaskField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MSPUtils {

    private static final Logger logger = LoggerFactory.getLogger(MSPUtils.class);

    private static final String TA_TAG = "created_by_task_adapter";
    private static final int RESOURCE_INDEX = 1;

    @SuppressWarnings("serial")
    private static Map<String, TaskField> TEXT_FIELDS_MAP = Collections.unmodifiableMap(new TreeMap<String, TaskField>() {
        {
            put(TaskField.TEXT1.toString(), TaskField.TEXT1);
            put(TaskField.TEXT2.toString(), TaskField.TEXT2);
            put(TaskField.TEXT3.toString(), TaskField.TEXT3);
            put(TaskField.TEXT4.toString(), TaskField.TEXT4);
            put(TaskField.TEXT5.toString(), TaskField.TEXT5);
            put(TaskField.TEXT6.toString(), TaskField.TEXT6);
            put(TaskField.TEXT7.toString(), TaskField.TEXT7);
            put(TaskField.TEXT8.toString(), TaskField.TEXT8);
            put(TaskField.TEXT9.toString(), TaskField.TEXT9);
            put(TaskField.TEXT10.toString(), TaskField.TEXT10);
            put(TaskField.TEXT11.toString(), TaskField.TEXT11);
            put(TaskField.TEXT12.toString(), TaskField.TEXT12);
            put(TaskField.TEXT13.toString(), TaskField.TEXT13);
            put(TaskField.TEXT14.toString(), TaskField.TEXT14);
            put(TaskField.TEXT15.toString(), TaskField.TEXT15);
            put(TaskField.TEXT16.toString(), TaskField.TEXT16);
            put(TaskField.TEXT17.toString(), TaskField.TEXT17);
            put(TaskField.TEXT18.toString(), TaskField.TEXT18);
            put(TaskField.TEXT19.toString(), TaskField.TEXT19);
            put(TaskField.TEXT20.toString(), TaskField.TEXT20);
            put(TaskField.TEXT21.toString(), TaskField.TEXT21);
            put(TaskField.TEXT22.toString(), TaskField.TEXT22);
            put(TaskField.TEXT23.toString(), TaskField.TEXT23);
            put(TaskField.TEXT24.toString(), TaskField.TEXT24);
            put(TaskField.TEXT25.toString(), TaskField.TEXT25);
            put(TaskField.TEXT26.toString(), TaskField.TEXT26);
            put(TaskField.TEXT27.toString(), TaskField.TEXT27);
            put(TaskField.TEXT28.toString(), TaskField.TEXT28);
            put(TaskField.TEXT29.toString(), TaskField.TEXT29);
            put(TaskField.TEXT30.toString(), TaskField.TEXT30);
        }
    });

    public static void markResourceAsOurs(Resource r) {
        r.setText(RESOURCE_INDEX, TA_TAG);
        r.setNotes("Resource created by Task Adapter application.\nPlease, do NOT change the name or 'Unique ID' field (UID)."
                + "\nThe UID is copied from an external system (like Redmine) and is required for 'update task' operations in Task Adapter."
                + "\nUID=" + r.getUniqueID());
    }

    public static boolean isResourceOurs(Resource r) {
        String textField1 = r.getText(RESOURCE_INDEX);
        return ((textField1 != null) && textField1.equals(TA_TAG));
    }

    public static String[] getTextFieldNamesAvailableForMapping() {
        Set<String> keys = TEXT_FIELDS_MAP.keySet();
        Set<String> copy = new HashSet<>(keys);
        copy.remove(MSPDefaultFields.FIELD_DURATION_UNDEFINED.getName());
        copy.remove(MSPDefaultFields.FIELD_WORK_UNDEFINED.getName());
        return copy.toArray(new String[copy.size()]);
    }

    public static TaskField getTaskFieldByName(String name) {
        return TEXT_FIELDS_MAP.get(name);
    }

    /**
     * Take absolute path to *.mpp file, convert it to *.xml and save to the same folder
     * @param mppFilePath absolute path to *.mpp file
     * @return new absolute path to .xml
     */
    public static String convertMppProjectFileToXml(String mppFilePath) {
        try {
            String outputAbsoluteFilePath = changeExtension(mppFilePath, ".xml");
            ProjectFile projectFile = new MSPFileReader().readFile(mppFilePath);
            return RealWriter.writeProject(outputAbsoluteFilePath, projectFile);
        } catch (Throwable e) {
            logger.error("error converting MPP file to XML: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * change extension in file name
     * @param originalName Full name of file
     * @param newExtension New extension like ".xml"
     * @return new file name
     */
    public static String changeExtension(String originalName, String newExtension) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot) + newExtension;
        } else {
            return originalName + newExtension;
        }
    }

    public static String getDefaultDueDate() {
        return TaskField.FINISH.toString();
    }

	public static final String NO_CONSTRAINT = "<no constraint>";

    public static String getEstimatedTimeDefaultMapping() {
        return TaskField.DURATION.toString();
    }

}
