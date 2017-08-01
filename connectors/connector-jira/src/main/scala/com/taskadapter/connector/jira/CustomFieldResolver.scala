package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Field

object CustomFieldResolver {
  def apply(): CustomFieldResolver = new CustomFieldResolver(Seq())
}

class CustomFieldResolver(fields: Iterable[Field]) {
  val mapNameToId = fields.map(f => f.getName -> f.getId).toMap

  def getId(name: String): String = {
    mapNameToId(name)
  }
}
