package com.taskadapter.connector.msp;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;

import java.io.File;
import java.util.HashMap;

public class MSPConfig extends ConnectorConfig {
    private static final long serialVersionUID = 1L;

    // TODO this is a UI element. move it to UI layer
    static final String DEFAULT_LABEL = "Microsoft Project";

    private String inputAbsoluteFilePath = "";
    private String outputAbsoluteFilePath = "";

    // TODO these seem unused. delete them?
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

    @Override
    public String toString() {
        return "MSPConfig [inputAbsoluteFilePath=" + inputAbsoluteFilePath + ", outputAbsoluteFilePath=" + outputAbsoluteFilePath + "]";
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
