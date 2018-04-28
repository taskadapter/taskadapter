package com.taskadapter.model;

public class GTaskDescriptor {

    // TODO REVIEW Probably it should not be a enum anymore. But something like
    // class <T> Field<T> {
    //      public Class getImplClass()
    //      public ? parseDefaultValue(String xxx)
    //      public JSon serializeDefaultValue(? q)
    //      public ? deserializeDefaultValue(J json)
    //      etc...
    // }
    public enum FIELD {
        ID, KEY, PARENT_KEY,
        SOURCE_SYSTEM_ID,
        CHILDREN, RELATIONS
    }
}
