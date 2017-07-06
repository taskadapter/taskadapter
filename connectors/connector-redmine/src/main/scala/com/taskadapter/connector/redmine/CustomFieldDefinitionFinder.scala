package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.redmineapi.bean.CustomFieldDefinition

import scala.collection.JavaConverters._

object CustomFieldDefinitionFinder {
  def findCustomFieldId(customFieldDefinitions: util.List[CustomFieldDefinition], fieldName: String) : Integer = {
    customFieldDefinitions.asScala.find(d => d.getName == fieldName).map(_.getId).orNull
  }
}
