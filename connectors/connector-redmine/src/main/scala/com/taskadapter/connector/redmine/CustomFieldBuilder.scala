package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.model.Field
import com.taskadapter.redmineapi.bean.{CustomFieldDefinition, CustomFieldFactory, Issue}

// TODO move to Redmine Java API?
object CustomFieldBuilder {

  def add(issue: Issue, customFieldDefinitions: util.List[CustomFieldDefinition], field: Field[_], value: String): Unit = {
    val customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, field)
    val customField = CustomFieldFactory.create(customFieldId, field.getFieldName(), value.toString)
    issue.addCustomField(customField)
  }
}
