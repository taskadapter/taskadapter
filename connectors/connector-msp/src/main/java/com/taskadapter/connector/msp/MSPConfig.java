package com.taskadapter.connector.msp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mapping;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Alexey Skorokhodov
 */
public class MSPConfig extends ConnectorConfig {
    private static final long serialVersionUID = 1L;

    static final String DEFAULT_LABEL = "Microsoft Project";

    private String inputFileName = "";
    private String outputFileName = "";

    public MSPConfig() {
        super();
        setLabel(DEFAULT_LABEL);
    }

    /**
     * @param absoluteFilePath absolute path to the MSP file name
     */
    public MSPConfig(String absoluteFilePath) {
        this.inputFileName = absoluteFilePath;
        this.outputFileName = absoluteFilePath;
        setLabel(DEFAULT_LABEL);
    }

    public MSPConfig(MSPConfig configToDeepClone) {
        super(configToDeepClone);
        setLabel(configToDeepClone.getLabel());

        this.inputFileName = configToDeepClone.getInputFileName();
        this.outputFileName = configToDeepClone.getOutputFileName();
    }

    /**
     * @return absolute path to the MSP file name
     */
    public String getInputFileName() {
        return inputFileName;
    }

    /**
     * @param fileName absolute path to the MSP file name
     */
    public void setInputFileName(String fileName) {
        this.inputFileName = fileName;
    }

    /**
     * @return absolute path to the MSP file name
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * @param outputFileName absolute path to the MSP file name
     */
    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    @Override
    public String getSourceLocation() {
        return inputFileName;
    }

    @Override
    public String getTargetLocation() {
        return outputFileName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((inputFileName == null) ? 0 : inputFileName.hashCode());
        result = prime * result
                + ((outputFileName == null) ? 0 : outputFileName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MSPConfig other = (MSPConfig) obj;
        if (inputFileName == null) {
            if (other.inputFileName != null)
                return false;
        } else if (!inputFileName.equals(other.inputFileName))
            return false;
        if (outputFileName == null) {
            if (other.outputFileName != null)
                return false;
        } else if (!outputFileName.equals(other.outputFileName))
            return false;
        return true;
    }

    /**
     * @return map: field name --> Mapping object.
     */
    @Override
    protected Map<FIELD, Mapping> generateDefaultFieldsMapping() {
        Map<FIELD, Mapping> fieldsMapping = new TreeMap<FIELD, Mapping>();
        fieldsMapping.put(FIELD.SUMMARY, new Mapping());
        fieldsMapping.put(FIELD.TASK_TYPE, new Mapping(true, MSPAvailableFieldsProvider.getDefaultTaskType()));

        // TODO set default values in MSP..Provider instead of using [0]
        String defaultEstimatedTimeOption = MSPAvailableFieldsProvider.getEstimatedTimeOptions()[0];
        fieldsMapping.put(FIELD.ESTIMATED_TIME, new Mapping(true, defaultEstimatedTimeOption));
        fieldsMapping.put(FIELD.DONE_RATIO, new Mapping());
        fieldsMapping.put(FIELD.ASSIGNEE, new Mapping());
        fieldsMapping.put(FIELD.DESCRIPTION, new Mapping());

        String defaultStartDateOption = MSPAvailableFieldsProvider.getStartDateOptions()[0];
        fieldsMapping.put(FIELD.START_DATE, new Mapping(false, defaultStartDateOption));

        String defaultDueDateOption = MSPAvailableFieldsProvider.getDueDateOptions()[0];
        fieldsMapping.put(FIELD.DUE_DATE, new Mapping(false, defaultDueDateOption));

        /*
           *  when "saveRemoteId" was by default TRUE for new configs,
           *  many users have complained that they exported tasks from an MSP file
           *  to Redmine, then deleted them in the Redmine and tried to re-export
           *  and got "issue with id... not found".
           *  it's better to have this option set to FALSE by default to avoid the confusion.
           */
        fieldsMapping.put(FIELD.REMOTE_ID, new Mapping(false, MSPAvailableFieldsProvider.getDefaultRemoteIdMapping()));
        fieldsMapping.put(FIELD.TASK_STATUS, new Mapping(false, MSPAvailableFieldsProvider.getDefaultTaskStatus()));

        return fieldsMapping;
    }

    @Override
    public String toString() {
        return "MSPConfig [inputFileName=" + inputFileName + ", outputFileName=" + outputFileName + "]";
    }

    @Override
    public void validateForLoad() throws ValidationException {
        if (inputFileName.isEmpty()) {
            throw new ValidationException("Please provide the input file name in MSP config");
        }
    }

    @Override
    public void validateForSave() throws ValidationException {
        if (outputFileName.isEmpty()) {
            throw new ValidationException("Please provide the output file name in MSP config");
        }
    }

    @Override
    protected Priorities generateDefaultPriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;

            {
                put("100", 100);
                put("500", 500);
                put("700", 700);
                put("800", 800);
                put("1000", 1000);
            }
        });
    }
}
