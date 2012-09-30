package com.taskadapter.connector.msp;

import com.taskadapter.connector.MSPOutputFileNameNotSetException;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.io.File;
import java.util.HashMap;

public class MSPConfig extends ConnectorConfig {
    private static final long serialVersionUID = 1L;

    // TODO this is a UI element. move it to UI layer
    static final String DEFAULT_LABEL = "Microsoft Project";

    private String inputAbsoluteFilePath = "";
    private String outputAbsoluteFilePath = "";

    public static final String DEFAULT_OUTPUT_FILE_NAME = "MSP_export_";
    public static final String DEFAULT_OUTPUT_SUFFIX = ".xml";


    public MSPConfig() {
        super();
        setLabel(DEFAULT_LABEL);
    }

    /**
     * @param absoluteFilePath absolute path to the MSP file name
     */
    public MSPConfig(String absoluteFilePath) {
        this.inputAbsoluteFilePath = absoluteFilePath;
        this.outputAbsoluteFilePath = absoluteFilePath;
        setLabel(DEFAULT_LABEL);
    }

    public MSPConfig(MSPConfig configToDeepClone) {
        super(configToDeepClone);
        setLabel(configToDeepClone.getLabel());

        this.inputAbsoluteFilePath = configToDeepClone.getInputAbsoluteFilePath();
        this.outputAbsoluteFilePath = configToDeepClone.getOutputAbsoluteFilePath();
    }

    /**
     * @return absolute path to the MSP file name
     */
    public String getInputAbsoluteFilePath() {
        return inputAbsoluteFilePath;
    }

    /**
     * @param fileName absolute path to the MSP file name
     */
    public void setInputAbsoluteFilePath(String fileName) {
        this.inputAbsoluteFilePath = fileName;
    }

    /**
     * @return absolute path to the MSP file name
     */
    public String getOutputAbsoluteFilePath() {
        return outputAbsoluteFilePath;
    }

    /**
     * @param outputAbsoluteFilePath absolute path to the MSP file name
     */
    public void setOutputAbsoluteFilePath(String outputAbsoluteFilePath) {
        this.outputAbsoluteFilePath = outputAbsoluteFilePath;
    }

    @Override
    public String getSourceLocation() {
        return new File(inputAbsoluteFilePath).getName();
    }

    @Override
    public String getTargetLocation() {
        return new File(outputAbsoluteFilePath).getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((inputAbsoluteFilePath == null) ? 0 : inputAbsoluteFilePath.hashCode());
        result = prime * result
                + ((outputAbsoluteFilePath == null) ? 0 : outputAbsoluteFilePath.hashCode());
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
        if (inputAbsoluteFilePath == null) {
            if (other.inputAbsoluteFilePath != null)
                return false;
        } else if (!inputAbsoluteFilePath.equals(other.inputAbsoluteFilePath))
            return false;
        if (outputAbsoluteFilePath == null) {
            if (other.outputAbsoluteFilePath != null)
                return false;
        } else if (!outputAbsoluteFilePath.equals(other.outputAbsoluteFilePath))
            return false;
        return true;
    }

    /**
     * @return map: field name --> Mapping object.
     */
    @Override
    protected Mappings generateDefaultFieldsMapping() {
    	final Mappings result = new Mappings();
    	result.setMapping(FIELD.SUMMARY, true, null);
        result.setMapping(FIELD.TASK_TYPE, true, MSPUtils.getDefaultTaskType());

        // TODO set default values in MSP..Provider instead of using [0]
        String defaultEstimatedTimeOption = MSPUtils.getEstimatedTimeOptions()[0];
    	result.setMapping(FIELD.ESTIMATED_TIME, true, defaultEstimatedTimeOption);
    	result.setMapping(FIELD.DONE_RATIO, true, null);
    	result.setMapping(FIELD.ASSIGNEE, true, null);
    	result.setMapping(FIELD.DESCRIPTION, true, null);
    	
        String defaultStartDateOption = MSPUtils.getStartDateOptions()[0];
    	result.setMapping(FIELD.START_DATE, false, defaultStartDateOption);
    	
        String defaultDueDateOption = MSPUtils.getDueDateOptions()[0];
    	result.setMapping(FIELD.DUE_DATE, false, defaultDueDateOption);

        /*
         *  when "saveRemoteId" was by default TRUE for new configs,
         *  many users have complained that they exported tasks from an MSP file
         *  to Redmine, then deleted them in the Redmine and tried to re-export
         *  and got "issue with id... not found".
         *  it's better to have this option set to FALSE by default to avoid the confusion.
         */
    	result.setMapping(FIELD.REMOTE_ID, false, MSPUtils.getDefaultRemoteIdMapping());
    	result.setMapping(FIELD.TASK_STATUS, false, MSPUtils.getDefaultTaskStatus());


        return result;
    }

    @Override
    public String toString() {
        return "MSPConfig [inputAbsoluteFilePath=" + inputAbsoluteFilePath + ", outputAbsoluteFilePath=" + outputAbsoluteFilePath + "]";
    }

    @Override
    public void validateForLoad() throws ValidationException {
        if (inputAbsoluteFilePath.isEmpty()) {
            throw new ValidationException("Please provide the input file name in MSP config");
        }
    }

    @Override
    public void validateForSave() throws ValidationException {
        if (outputAbsoluteFilePath.isEmpty()) {
            throw new MSPOutputFileNameNotSetException();
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
