package com.taskadapter.config;

public class TAFile {
    private String absoluteFilePath;
    private String configLabel;
    private ConnectorDataHolder connectorDataHolder1;
    private ConnectorDataHolder connectorDataHolder2;

    /**
     * this no-args constructor is required for GSon.
     */
    public TAFile() {
    }

    public TAFile(String configLabel, ConnectorDataHolder d1, ConnectorDataHolder d2) {
        this.configLabel = configLabel;
        this.connectorDataHolder1 = d1;
        this.connectorDataHolder2 = d2;
    }

    // TODO document this semi-deep-clone constructor or better yet - delete it!
    public TAFile(TAFile source) {
        this(source.getConfigLabel(), source.getConnectorDataHolder1(), source.getConnectorDataHolder2());
        this.absoluteFilePath = source.getAbsoluteFilePath();
    }

    public void setConfigLabel(String configLabel) {
        this.configLabel = configLabel;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public ConnectorDataHolder getConnectorDataHolder1() {
        return connectorDataHolder1;
    }

    public void setConnectorDataHolder1(ConnectorDataHolder connectorDataHolder1) {
        this.connectorDataHolder1 = connectorDataHolder1;
    }

    public ConnectorDataHolder getConnectorDataHolder2() {
        return connectorDataHolder2;
    }

    public void setConnectorDataHolder2(ConnectorDataHolder connectorDataHolder2) {
        this.connectorDataHolder2 = connectorDataHolder2;
    }

    public String getConfigLabel() {
        return configLabel;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAFile taFile = (TAFile) o;

        if (absoluteFilePath != null ? !absoluteFilePath.equals(taFile.absoluteFilePath) : taFile.absoluteFilePath != null)
            return false;
        if (configLabel != null ? !configLabel.equals(taFile.configLabel) : taFile.configLabel != null) return false;
        if (connectorDataHolder1 != null ? !connectorDataHolder1.equals(taFile.connectorDataHolder1) : taFile.connectorDataHolder1 != null)
            return false;
        if (connectorDataHolder2 != null ? !connectorDataHolder2.equals(taFile.connectorDataHolder2) : taFile.connectorDataHolder2 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = absoluteFilePath != null ? absoluteFilePath.hashCode() : 0;
        result = 31 * result + (configLabel != null ? configLabel.hashCode() : 0);
        result = 31 * result + (connectorDataHolder1 != null ? connectorDataHolder1.hashCode() : 0);
        result = 31 * result + (connectorDataHolder2 != null ? connectorDataHolder2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return configLabel;
    }

}
