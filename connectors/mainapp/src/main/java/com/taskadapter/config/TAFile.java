package com.taskadapter.config;


public class TAFile {
    private String name;
    private ConnectorDataHolder connector1;
    private ConnectorDataHolder connector2;

    /**
     * this no-args constructor is required for GSon.
     */
    public TAFile() {
    }

    public TAFile(String name, ConnectorDataHolder d1, ConnectorDataHolder d2) {
        this.name = name;
        this.connector1 = d1;
        this.connector2 = d2;
    }

    public TAFile(TAFile source) {
        this(source.getName(), source.getConnector1(), source.getConnector2());
    }

    public ConnectorDataHolder getConnector1() {
        return connector1;
    }

    public void setConnector1(ConnectorDataHolder connector1) {
        this.connector1 = connector1;
    }

    public ConnectorDataHolder getConnector2() {
        return connector2;
    }

    public void setConnector2(ConnectorDataHolder connector2) {
        this.connector2 = connector2;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((connector1 == null) ? 0 : connector1.hashCode());
        result = prime * result
                + ((connector2 == null) ? 0 : connector2.hashCode());
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
        if (connector1 == null) {
            if (other.connector1 != null) {
                return false;
            }
        } else if (!connector1.equals(other.connector1)) {
            return false;
        }
        if (connector2 == null) {
            if (other.connector2 != null) {
                return false;
            }
        } else if (!connector2.equals(other.connector2)) {
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
