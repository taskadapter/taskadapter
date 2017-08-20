package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Field

object CustomFieldResolver {
  def apply(): CustomFieldResolver = new CustomFieldResolver(Seq())
}

/**
  * @param fullIdForSave e.g. 'customfield_10100'
  * @param typeName sample values: 'array', 'any', 'option', 'progress', 'user', 'number', 'datetype', ...
  * @param itemsTypeIfArray "null" for any [typeName] other than 'array'. for arrays sample values are:
  *                         "string","attachment", "version", "component", "version", "issueLinks", "worklog", ...
  */
case class JiraFieldDefinition(id: Long, fullIdForSave: String, typeName: String, itemsTypeIfArray: Option[String])

/**
  * Sample custom field loaded from JIRA 7:
  *
  * ```
   Field{id=customfield_10100, name=label_environment, fieldType=CUSTOM, orderable=true, navigable=true, searchable=true,
         schema=FieldSchema{type=array, items=string, system=null,
                            custom=com.atlassian.jira.plugin.system.customfieldtypes:labels, customId=10100}}
  * ```
  */
class CustomFieldResolver(fields: Iterable[Field]) {
  val mapNameToSchema = fields
    .filter(f => f.getSchema != null)
    .map(f => f.getName -> JiraFieldDefinition(f.getSchema.getCustomId, f.getId, f.getSchema.getType, Option(f.getSchema.getItems)))
    .toMap

  def getId(name: String): Option[JiraFieldDefinition] = {
    mapNameToSchema.get(name)
  }
}
