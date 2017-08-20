package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.redmineapi.bean.{CustomFieldDefinition, CustomFieldFactory, Issue}

// TODO move to Redmine Java API?
object CustomFieldBuilder {

  def add(issue: Issue, customFieldDefinitions: util.List[CustomFieldDefinition], fieldName: String, value: String): Unit = {
    val customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, fieldName)
    val customField = CustomFieldFactory.create(customFieldId, fieldName, value.toString)
    issue.addCustomField(customField)
  }
}
