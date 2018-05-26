package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.model.Field
import com.taskadapter.redmineapi.bean.CustomFieldDefinition

import scala.collection.JavaConverters._

object CustomFieldDefinitionFinder {
  def findCustomFieldId(customFieldDefinitions: util.List[CustomFieldDefinition], field: Field[_]) : Integer = {
    customFieldDefinitions.asScala.find(d => d.getName == field.name).map(_.getId).orNull
  }
}
