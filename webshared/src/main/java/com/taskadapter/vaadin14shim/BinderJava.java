package com.taskadapter.vaadin14shim;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinderJava {

    /**
     * find the field (including private ones and those from superclasses) on the object.
     */
    static Field getField(Object instance, String fieldName) {
        Class<?> aClass = instance.getClass();
        Iterable<Field> fields = getFieldsUpTo(aClass);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        throw new IllegalArgumentException("field not found");
    }

    private static Iterable<Field> getFieldsUpTo(Class<?> startClass) {
        return getFieldsUpTo(startClass, null);
    }

    private static Iterable<Field> getFieldsUpTo(Class<?> startClass,
                                                 Class<?> exclusiveParent) {

        List<Field> currentClassFields = new ArrayList(Arrays.asList(startClass.getDeclaredFields()));
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields =
                    (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }
}
