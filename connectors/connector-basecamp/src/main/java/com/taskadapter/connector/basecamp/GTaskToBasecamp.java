package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.common.data.ConnectorConverter;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.json.JSONException;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GTaskToBasecamp implements ConnectorConverter<GTask, BasecampTaskWrapper> {
    private final List<GUser> users;

    public GTaskToBasecamp(List<GUser> users) {
        this.users = users;
    }

    /**
     * Convert a task from source to target format.
     *
     * @param source source object to convert.
     * @return converted object
     */
    @Override
    public BasecampTaskWrapper convert(GTask source) throws ConnectorException {
        var stringWriter = new StringWriter();
        try (stringWriter) {
            var writer = new JSONWriter(stringWriter);
            writer.object();
            Map<Field<?>, Object> fields = source.getFields();
            Set<Map.Entry<Field<?>, Object>> entries = fields.entrySet();
            for (Map.Entry<Field<?>, Object> entry : entries) {
                var field = entry.getKey();
                var value = entry.getValue();
                try {
                    processField(writer, field, value);
                } catch (Exception e) {
                    throw new FieldConversionException(BasecampConnector.ID, field, value, e.getMessage());
                }
            }
            writer.endObject();
        } catch (FieldConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new ConnectorException(e);
        }
        return new BasecampTaskWrapper(source.getKey(), stringWriter.toString(), source.getValue(AllFields.doneRatio));
    }

    private void processField(JSONWriter writer, Field<?> field, Object value) throws JSONException {
        if (field.equals(BasecampField.content)) {
            var stringValue = (String) value;
            JsonUtils.writeOpt(writer, "content", stringValue);
        } else if (field.equals(AllFields.doneRatio)) {
            var completed = value != null && ((Float) value >= 100);
            JsonUtils.writeOpt(writer, "completed", completed);
        } else if (field.equals(AllFields.dueDate)) {
            JsonUtils.writeShort(writer, "due_at", (Date) value);
        } else if (field.equals(AllFields.assigneeFullName)) {
            writeAssignee(writer, (String) value);
        }
        // ignore unknown fields

    }

    private void writeAssignee(JSONWriter writer, String fullName) throws JSONException {
        var field = "assignee";
        if (fullName == null) {
            writer.key(field).value(null);
            return;
        }
        var resolvedAssignee = users.stream().filter(user -> user.getDisplayName().equals(fullName)).findFirst();
        if (resolvedAssignee.isEmpty() || resolvedAssignee.get().getId() == null) {
            return;
        }
        writer.key(field).object().key("type").value("Person");
        writer.key("id").value(resolvedAssignee.get().getId().intValue());
        writer.endObject();
    }
}
