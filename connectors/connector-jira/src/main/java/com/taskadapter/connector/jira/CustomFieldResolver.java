package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.taskadapter.model.CustomListString;
import com.taskadapter.model.CustomString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Sample custom field loaded from JIRA 7:
 *
 * <pre>
 * Field{id=customfield_10100, name=label_environment, fieldType=CUSTOM, orderable=true, navigable=true, searchable=true,
 * schema=FieldSchema{type=array, items=string, system=null,
 * custom=com.atlassian.jira.plugin.system.customfieldtypes:labels, customId=10100}}
 * </pre>>
 */
public class CustomFieldResolver {

    public static CustomFieldResolver apply() {
        return new CustomFieldResolver(List.of());
    }

    private final Map<String, JiraFieldDefinition> nameToCustomFieldDefinition;

    public CustomFieldResolver(Iterable<Field> fields) {
        nameToCustomFieldDefinition = StreamSupport.stream(fields.spliterator(), false)
                .filter(f -> f.getSchema() != null)
                // only keep custom fields
                .filter(f -> f.getSchema().isCustom())
                .collect(Collectors.toMap(Field::getName,
                        f -> new JiraFieldDefinition(f.getSchema().getCustomId(),
                                f.getName(), f.getId(), f.getSchema().getType(),
                                Optional.ofNullable(f.getSchema().getItems())))
                );
    }

    Optional<JiraFieldDefinition> getId(String name) {
        return Optional.ofNullable(nameToCustomFieldDefinition.get(name));
    }

    Optional<com.taskadapter.model.Field<?>> getField(IssueField jiraField) {
        var fieldId = getId(jiraField.getName());
        if (fieldId.isEmpty()) {
            return Optional.empty();
        }
        JiraFieldDefinition definition = fieldId.get();
        return switch (definition.getTypeName()) {
            case "string", "any" -> Optional.of(new CustomString(definition.getFieldName()));
            case "array" -> Optional.of(new CustomListString(definition.getFieldName()));
            default -> Optional.empty();
        };
    }
}
