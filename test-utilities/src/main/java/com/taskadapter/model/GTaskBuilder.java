package com.taskadapter.model;

import com.taskadapter.connector.testlib.RandomStringGenerator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GTaskBuilder {
    private final GTask task = new GTask();

    public static GTask gtaskWithRandom(String fieldName) {
        return new GTaskBuilder().withRandom(Field.apply(fieldName)).build();
    }

    public static GTask gtaskWithRandomJava(Field<?> field) {
        return new GTaskBuilder().withRandom(field).build();
    }

    public static GTask gtaskWithRandom(Field<?> field) {
        return new GTaskBuilder().withRandom(field).build();
    }

    public static GTask withSummary(String value) {
        return new GTaskBuilder().withField(Summary$.MODULE$, value).build();
    }

    public static GTask withSummary() {
        var value = new Random().nextDouble() + "";
        return withSummary(value);
    }

    public static List<GTask> getTwo() {
        return List.of(withSummary(), withSummary());
    }

    public <T> GTaskBuilder withField(Field<T> field, T value) {
        task.setValue(field, value);
        return this;
    }

    public GTaskBuilder withAssigneeLogin(String loginName) {
        task.setValue(AssigneeLoginName$.MODULE$, loginName);
        return this;
    }

    public GTaskBuilder withRandom(Field<?> field) {
        if (field instanceof CustomDate) {
            task.setValue((CustomDate) field, getDateRoundedToMinutes());
            return this;
        }

        if (field instanceof CustomFloat) {
            var value = new Random().nextFloat() * 100;
            //       round to 2 digits
            var doubleValue = Math.round(value * 100.0) / 100.0;
            task.setValue((CustomFloat) field, (float) doubleValue);
            return this;
        }
        //      case "GUser" -> new GUser(null, Random.nextString(3), Random.nextString(10))
        //      case "String" -> "value " + new Date().getTime

        if (field instanceof Summary$) {
            task.setValue(Summary$.MODULE$, randomStr());
            return this;
        }
        if (field instanceof Description$) {
            task.setValue(Description$.MODULE$, randomStr());
            return this;
        }
        if (field instanceof AssigneeLoginName$) {
            task.setValue(AssigneeLoginName$.MODULE$, RandomStringGenerator.randomAlphaNumeric(10));
            return this;
        }
        if (field instanceof AssigneeFullName$) {
            task.setValue(AssigneeFullName$.MODULE$, RandomStringGenerator.randomAlphaNumeric(10));
            return this;
        }

        if (field instanceof CustomString) {
            task.setValue((CustomString) field, "value " + new Date().getTime());
            return this;
        }
        throw new RuntimeException("unknown field type: " + field.getClass());
    }

    public GTask build() {
        return task;
    }

    public static Date getDateRoundedToMinutes() {
        var cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String randomStr() {
        return "value " + new Date().getTime();
    }
}
