package com.taskadapter.connector.definition;

public class FileSetup implements ConnectorSetup {
    private String connectorId;
    private String id;
    private String label;
    private String sourceFile;
    private String targetFile;

    public FileSetup() {
    }

    public static FileSetup apply(String connectorId,
                                  String label,
                                  String sourceFile,
                                  String targetFile) {
        var setup = new FileSetup();
        setup.setConnectorId(connectorId);
        setup.setLabel(label);
        setup.setSourceFile(sourceFile);
        setup.setTargetFile(targetFile);
        return setup;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }
}
