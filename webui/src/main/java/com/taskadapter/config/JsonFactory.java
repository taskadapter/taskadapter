package com.taskadapter.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.common.ConfigUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import com.taskadapter.model.CustomFloat;
import com.taskadapter.model.CustomListString;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonFactory {
    private static final Gson gson = ConfigUtils.createDefaultGson();

    public static String toString(List<FieldMapping<?>> mappings) {
        var list = mappings.stream().map(m -> {
            var defaultValueJsonString = m.getDefaultValue() == null ? null
                    : gson.toJson(m.getDefaultValue());
            var field1Block = optionalToString(m.getFieldInConnector1());
            var field2Block = optionalToString(m.getFieldInConnector2());
            return "{ \"fieldInConnector1\": " + field1Block + ", \"fieldInConnector2\": " + field2Block + ", \"defaultValue\" : " + defaultValueJsonString + ", \"selected\": \"" + m.isSelected() + "\" }";
        }).collect(Collectors.toList());
        var mappingsList = String.join(",", list);
        return "[ " + mappingsList + " ]";
    }

    public static List<FieldMapping<?>> fromJsonString(String jsonString) {
        Type type = new TypeToken<List<Map<String, ?>>>() {
        }.getType();
        List<Map<String, ?>> list = new Gson().fromJson(jsonString, type);
        return list.stream()
                .map(JsonFactory::mapToFieldMapping)
                .collect(Collectors.toList());
    }

    private static FieldMapping<?> mapToFieldMapping(Map<String, ?> map) {
        var fieldInConnector1 = (Map<String, ?>) map.get("fieldInConnector1");
        var fieldInConnector2 = (Map<String, ?>) map.get("fieldInConnector2");
        var selected = Boolean.parseBoolean(map.get("selected").toString());
        var defaultValueObject = map.getOrDefault("defaultValue", null);

        var field1 = fieldFromJson(fieldInConnector1);
        var field2 = fieldFromJson(fieldInConnector2);
        return new FieldMapping(field1,
                field2,
                selected,
                (String) defaultValueObject
        );
    }

    private static <T> Optional<Field<T>> fieldFromJson(Map<String, ?> json) {
        if (json == null) {
            return Optional.empty();
        }
        var fieldName = (String) json.get("name");
        var gType = json.get("type").toString();
        // temporary adapter to recognize legacy (pre- March-2021) fields ending with "$" (leftover from Scala)
        gType = gType.replace("$", "");
        var result = switch (gType) {
            case "AssigneeLoginName" -> AllFields.assigneeLoginName;
            case "AssigneeFullName" -> AllFields.assigneeFullName;
            case "Children" -> AllFields.children;
            case "Components" -> AllFields.components;
            case "ClosedOn" -> AllFields.closedOn;
            case "CreatedOn" -> AllFields.createdOn;
            case "CustomString" -> new CustomString(fieldName);
            case "CustomFloat" -> new CustomFloat(fieldName);
            case "CustomDate" -> new CustomDate(fieldName);
            case "CustomSeqString" -> new CustomListString(fieldName);
            case "Description" -> AllFields.description;
            case "DoneRatio" -> AllFields.doneRatio;
            case "DueDate" -> AllFields.dueDate;
            case "EstimatedTime" -> AllFields.estimatedTime;
//      case "SpentTime" -> SpentTime;
            case "Id" -> AllFields.id;
            case "Key" -> AllFields.key;
            case "ParentKey" -> AllFields.parentKey;
            case "Priority" -> AllFields.priority;
            case "Relations" -> AllFields.relations;
            case "ReporterFullName" -> AllFields.reporterFullName;
            case "ReporterLoginName" -> AllFields.reporterLoginName;
            case "SourceSystemId" -> AllFields.sourceSystemId;
            case "StartDate" -> AllFields.startDate;
            case "Summary" -> AllFields.summary;
            case "TaskStatus" -> AllFields.taskStatus;
            case "TaskType" -> AllFields.taskType;
            case "TargetVersion" -> AllFields.targetVersion;
            case "UpdatedOn" -> AllFields.updatedOn;
            default -> throw new RuntimeException("unknown type: " + gType);
        };
        return Optional.of((Field<T>) result);
    }

    private static String optionalToString(Optional<? extends Field> f) {
        if (f.isEmpty()) {
            return "null";
        } else {
            var typeName = f.get().getClass().getSimpleName();
            var value = f.get().getFieldName();
            return "{ \"type\" : \"" + typeName + "\", \"name\": \"" + value + "\" }";
        }
    }
}
