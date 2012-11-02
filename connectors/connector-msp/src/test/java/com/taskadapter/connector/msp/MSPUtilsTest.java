package com.taskadapter.connector.msp;

import com.taskadapter.connector.msp.write.MSXMLFileWriter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MSPUtilsTest {
    @Test
    public void getAllTextFieldNamesDoesNotIncludeFieldsWeUseInternally() throws Exception {
        String[] allTextFieldNames = MSPUtils.getTextFieldNamesAvailableForMapping();
        List<String> collection = Arrays.asList(allTextFieldNames);
        assertThat(collection, not(contains(MSXMLFileWriter.FIELD_DURATION_UNDEFINED.getName())));
        assertThat(collection, not(contains(MSXMLFileWriter.FIELD_WORK_UNDEFINED.getName())));
    }

    public static Matcher contains(final Object expected) {
        return new BaseMatcher() {
            protected Object theExpected = expected;
            public boolean matches(Object o) {
                return ((Collection) o).contains(expected);
            }
            public void describeTo(Description description) {
                description.appendText(theExpected.toString());
            }
        };
    }
}
