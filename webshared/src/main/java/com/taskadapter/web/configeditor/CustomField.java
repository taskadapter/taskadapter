package com.taskadapter.web.configeditor;

/**
 * @author Igor Laishen
 */
public class CustomField {
    private String id;
    private String value;

    public CustomField () {
    }

    public CustomField (String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
