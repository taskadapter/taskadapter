package com.taskadapter.model;

import com.google.common.base.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class DateTypeTag implements FieldParser<Date> {
    /**
     * Format for dates in "default value if empty " fields on "Task Fields Mapping" panel.
     */
    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy MM dd");

    @Override
    public Date fromString(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        try {
            return DATE_PARSER.parse(str);
        } catch (ParseException e) {
            // TODO 14 another exception type?
            throw new RuntimeException(e);
        }
    }
}

class StringTypeTag implements FieldParser<String> {
    @Override
    public String fromString(String str) {
        return str;
    }
}

class GUserTypeTag implements FieldParser<GUser> {
    @Override
    public GUser fromString(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        return new GUser().setLoginName(str);
    }
}

class ListStringTypeTag implements FieldParser<List<String>> {
    @Override
    public List<String> fromString(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(str.split(" ")));
    }
}

class LongTypeTag implements FieldParser<Long> {
    @Override
    public Long fromString(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        return Long.parseLong(str);
    }
}

class FloatTypeTag implements FieldParser<Float> {
    @Override
    public Float fromString(String str) {
        return Strings.isNullOrEmpty(str) ? null : Float.parseFloat(str);
    }
}

class IntegerTypeTag implements FieldParser<Integer> {
    @Override
    public Integer fromString(String str) {

        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        return Integer.parseInt(str);
    }
}