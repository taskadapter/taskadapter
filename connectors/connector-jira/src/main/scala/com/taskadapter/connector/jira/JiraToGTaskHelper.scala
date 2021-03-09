package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.{Issue, IssueField}
import com.taskadapter.model.{Field, GTask}
import org.codehaus.jettison.json.{JSONArray, JSONException, JSONObject}
import org.slf4j.LoggerFactory

object JiraToGTaskHelper {
  private val logger = LoggerFactory.getLogger(classOf[JiraToGTask])

  def processCustomFields(resolver: CustomFieldResolver, issue: Issue, task: GTask): Unit = {
    issue.getFields.forEach((f: IssueField) => {
      def foo(f: IssueField) = if (f.getId.startsWith("customfield")) {
        // custom field
        val maybeField = resolver.getField(f)
        if (maybeField.isDefined) {
          processOneField(maybeField.get, task, f.getValue)
        }
      }
      foo(f)
    })
  }

  private def processOneField[T](field: Field[T], task: GTask, nativeValue: Any) : Unit = {
    try {
      val value = convertToGenericValue(field, nativeValue)
      task.setValue(field, value)
    } catch {
      case e: JSONException =>
        logger.error("Exception while converting JIRA value to generic one: ", e)
    }
  }

  @throws[JSONException]
  private def convertToGenericValue[T](field: Field[T], nativeValue: Any): T = {
    if (nativeValue == null) return null.asInstanceOf[T]

    val value = nativeValue match {
      case x: JSONObject => parseJsonObject(x)
      case x: JSONArray => parseJsonArray(field, x)
      case _ => nativeValue.toString
    }
    value.asInstanceOf[T]
  }

  private def parseJsonArray[T](field: Field[T], array: JSONArray): T = {
    val result = new java.util.ArrayList[T]()
    for (i <- 0 until array.length()) {
      val o = array.get(i)
      result.add(convertToGenericValue(field, o))
    }
    result.asInstanceOf[T]
  }

  private def parseJsonObject(jsonObject: JSONObject): String = {
    var value = ""
    try {
      value = jsonObject.getString("value")
    } catch {
      case e: Exception => value = ""
    }
    value
  }
}
