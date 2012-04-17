package com.taskadapter.config;

public class TAFile {
    private String name;
    private ConnectorDataHolder connectorDataHolder1;
    private ConnectorDataHolder connectorDataHolder2;

    /**
     * this no-args constructor is required for GSon.
     */
    public TAFile() {
    }

    public TAFile(String name, ConnectorDataHolder d1, ConnectorDataHolder d2) {
        this.name = name;
        this.connectorDataHolder1 = d1;
        this.connectorDataHolder2 = d2;
    }

    // TODO document this semi-deep-clone constructor or better yet - delete it!
    public TAFile(TAFile source) {
        this(source.getName(), source.getConnectorDataHolder1(), source.getConnectorDataHolder2());
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

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((connectorDataHolder1 == null) ? 0 : connectorDataHolder1.hashCode());
        result = prime * result
                + ((connectorDataHolder2 == null) ? 0 : connectorDataHolder2.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TAFile other = (TAFile) obj;
        if (connectorDataHolder1 == null) {
            if (other.connectorDataHolder1 != null) {
                return false;
            }
        } else if (!connectorDataHolder1.equals(other.connectorDataHolder1)) {
            return false;
        }
        if (connectorDataHolder2 == null) {
            if (other.connectorDataHolder2 != null) {
                return false;
            }
        } else if (!connectorDataHolder2.equals(other.connectorDataHolder2)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
