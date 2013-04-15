package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTaskDescriptor.FIELD;

final class StandardOutputContext implements OutputContext {

    private final Mappings mappings;

    public StandardOutputContext(Mappings mappings) {
        this.mappings = mappings;
    }

    @Override
    public String getXmlName(FIELD field) throws ConnectorException {
        if (!mappings.isFieldSelected(field)) {
            return null;
        }
        switch (field) {
            case DESCRIPTION:
                return "content";
            case SUMMARY:
                return "content";
            case DONE_RATIO:
                return "completed";
            case DUE_DATE:
                return "due-at";
            case ASSIGNEE:
                return "assignee";
            default:
                throw new ConnectorException("Unknown field: " + field);
        }
    }

}
