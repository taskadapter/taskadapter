package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import org.w3c.dom.Element

object BasecampClassicToGTask {
  def parseTask(obj: Element): GTask = {
    val result = new GTask
    val longId = XmlUtils.getIntElt(obj, "id").toLong
    result.setId(longId)
    val stringId = XmlUtils.getStringElt(obj, "id")
    result.setKey(stringId)
    result.setSourceSystemId(new TaskId(longId, stringId))
    result.setValue(BasecampClassicField.content, XmlUtils.getStringElt(obj, "content"))

    val compl = XmlUtils.getOptBool(obj, "completed")
    val floatCompletionValue : java.lang.Float = if (compl) 100f else 0f
    result.setValue(DoneRatio, floatCompletionValue)

    result.setValue(DueDate, XmlUtils.getOptLongDate(obj, "due-at"))
    result.setValue(CreatedOn, XmlUtils.getOptLongDate(obj, "created-at"))
    result.setValue(UpdatedOn, XmlUtils.getOptLongDate(obj, "updated-at"))
    result.setValue(ClosedOn, XmlUtils.getOptLongDate(obj, "completed-at"))
    val rpp = XmlUtils.getOptString(obj, "responsible-party-type")
    if ("Person" == rpp) {
      val displayName = XmlUtils.getStringElt(obj, "responsible-party-name")
      val idUnusedNow = XmlUtils.getIntElt(obj, "responsible-party-id")
      result.setValue(AssigneeFullName, displayName)
    }
    result
  }

}
