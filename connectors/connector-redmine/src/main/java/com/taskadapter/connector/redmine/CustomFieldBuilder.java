package com.taskadapter.connector.redmine;

import com.taskadapter.model.Field;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;
import com.taskadapter.redmineapi.bean.CustomFieldFactory;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.List;

// TODO move to Redmine Java API?
public class CustomFieldBuilder {

    public static void add(Issue issue, List<CustomFieldDefinition> customFieldDefinitions, Field<?> field, String value) {
        var customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, field);
        var customField = CustomFieldFactory.create(customFieldId, field.getFieldName(), value);
        issue.addCustomField(customField);
    }
}
