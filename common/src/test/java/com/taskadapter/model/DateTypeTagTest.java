package com.taskadapter.model;

import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTypeTagTest {
    @Test
    public void canParseToDate() {
        Calendar expected = Calendar.getInstance();
        expected.set(2021, Calendar.DECEMBER, 25);
        expected.set(Calendar.HOUR_OF_DAY, 0);
        expected.set(Calendar.MINUTE, 0);
        expected.set(Calendar.SECOND, 0);
        expected.set(Calendar.MILLISECOND, 0);

        assertThat(
                new DateTypeTag().fromString("2021 12 25")
        ).isEqualTo(expected.getTime());
    }

    @Test
    public void parsesNullToNull() {
        assertThat(
                new DateTypeTag().fromString(null)
        ).isNull();
    }
}
