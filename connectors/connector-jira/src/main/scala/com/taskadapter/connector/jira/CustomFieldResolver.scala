package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Field

object CustomFieldResolver {
  def apply(): CustomFieldResolver = new CustomFieldResolver(Seq())
}

case class JiraFieldDefinition(fullIdForSave: String, typeName: String)

class CustomFieldResolver(fields: Iterable[Field]) {
  val mapNameToSchema = fields
    .filter(f => f.getSchema != null) // only custom fields will have schema. native fields are of type "JIRA"
    .map(f => f.getName -> JiraFieldDefinition(f.getId, f.getSchema.getType))
    .toMap

  def getId(name: String): Option[JiraFieldDefinition] = {
    mapNameToSchema.get(name)
  }
}
