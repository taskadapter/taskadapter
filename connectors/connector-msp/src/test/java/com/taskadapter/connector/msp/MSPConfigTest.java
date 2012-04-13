package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ValidationException;
import org.junit.Test;

public class MSPConfigTest {

    @Test(expected = ValidationException.class)
    public void noOutputFileNameFailsSaveValidation() throws ValidationException {
        MSPConfig config = new MSPConfig();
        config.validateForSave();
    }

    @Test(expected = ValidationException.class)
    public void noInputFileNameFailsValidation() throws ValidationException {
        MSPConfig config = new MSPConfig();
        config.validateForLoad();
    }

}
