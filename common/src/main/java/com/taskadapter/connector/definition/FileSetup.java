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
        return new FileSetup()
                .setConnectorId(connectorId)
                .setLabel(label)
                .setSourceFile(sourceFile)
                .setTargetFile(targetFile);
    }

    public String getConnectorId() {
        return connectorId;
    }

    public FileSetup setConnectorId(String connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public String getId() {
        return id;
    }

    public FileSetup setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public FileSetup setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public FileSetup setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public FileSetup setTargetFile(String targetFile) {
        this.targetFile = targetFile;
        return this;
    }
}
