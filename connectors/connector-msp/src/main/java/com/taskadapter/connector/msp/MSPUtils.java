package com.taskadapter.connector.msp;

import com.taskadapter.model.GTaskDescriptor.FIELD;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.TaskField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MSPUtils {

    private static final Logger logger = LoggerFactory.getLogger(MSPUtils.class);

    private static final String TA_TAG = "created_by_task_adapter";

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
        }
    });

    public static void markResourceAsOurs(Resource r) {
        r.setText1(TA_TAG);
        r.setNotes("Resource created by Task Adapter application.\nPlease, do NOT change the name or 'Unique ID' field (UID)."
                + "\nThe UID is copied from an external system (like Redmine) and is required for 'update task' operations in Task Adapter."
                + "\nUID=" + r.getUniqueID());
    }

    public static boolean isResourceOurs(Resource r) {
        String textField1 = r.getText1();
        return ((textField1 != null) && textField1.equals(TA_TAG));
    }

    public static boolean useWork(MSPConfig config) {
        String value = config.getFieldMappings().getMappedTo(FIELD.ESTIMATED_TIME);
        if (value == null) {
            throw new RuntimeException("Invalid MSP Config. Estimated time must be mapped to something.");
        }
        if (value.equals(TaskField.WORK.toString())) {
            return true;
        } else if (value.equals(TaskField.DURATION.toString())) {
            return false;
        } else {
            throw new RuntimeException("Invalid value for EstimatedTime in the config: " + value + ". Allowed values: " +
                    TaskField.WORK.toString() + ", " + TaskField.DURATION.toString());
        }
    }

    static String[] getAllTextFieldNames() {
        Set<String> keys = TEXT_FIELDS_MAP.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    static TaskField getTaskFieldByName(String name) {
        return TEXT_FIELDS_MAP.get(name);
    }

    /**
     * Take absolute path to *.mpp file, convert it to *.xml and save to the same folder
     * @param mppFilePath absolute path to *.mpp file
     * @return new absolute path to .xml
     */
    public static String convertMppProjectFileToXml(String mppFilePath) {
        try {
            ProjectFile projectFile = new MSPFileReader().readFile(mppFilePath);
            MSPConfig config = new MSPConfig();
            config.setOutputAbsoluteFilePath(changeExtension(mppFilePath, ".xml"));
            return new MSXMLFileWriter(config).writeProject(projectFile);
        } catch (Exception e) {
            logger.error("error converting MPP file to XML: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * change extension in file name
     * @param originalName Full name of file
     * @param newExtension New extenstion like ".xml"
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

	public static String[] getEstimatedTimeOptions() {
	    return new String[]{TaskField.DURATION.toString(), TaskField.WORK.toString()};
	}

	public static String[] getDueDateOptions() {
	    return new String[]{TaskField.FINISH.toString(),
	            TaskField.DEADLINE.toString()};
	}

	public static String[] getStartDateOptions() {
	    String[] options = new String[ConstraintType.values().length + 1];
	    options[0] = MSPUtils.NO_CONSTRAINT;
	    int i = 1;
	    for (ConstraintType type : ConstraintType.values()) {
	        options[i++] = type.name();
	    }
	    return options;
	}

	public static String getDefaultRemoteIdMapping() {
	    return TaskField.TEXT22.toString();
	}

	public static String getDefaultTaskType() {
	    return TaskField.TEXT23.toString();
	}

	public static String getDefaultTaskStatus() {
	    return TaskField.TEXT24.toString();
	}

	public static final String NO_CONSTRAINT = "<no constraint>";
}
