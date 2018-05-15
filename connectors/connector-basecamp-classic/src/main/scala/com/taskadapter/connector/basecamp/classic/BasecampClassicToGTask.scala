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
    result.setSourceSystemId(TaskId(longId, stringId))
    result.setValue(BasecampClassicField.content, XmlUtils.getStringElt(obj, "content"))

    val compl = XmlUtils.getOptBool(obj, "completed")
    result.setValue(DoneRatio, if (compl) 100f else 0f)

    result.setValue(DueDate, XmlUtils.getOptLongDate(obj, "due-at"))
    result.setValue(CreatedOn, XmlUtils.getOptLongDate(obj, "created-at"))
    result.setValue(UpdatedOn, XmlUtils.getOptLongDate(obj, "updated-at"))
    result.setValue(ClosedOn, XmlUtils.getOptLongDate(obj, "completed-at"))
    val rpp = XmlUtils.getOptString(obj, "responsible-party-type")
    if ("Person" == rpp) {
      val looser = new GUser
      looser.setDisplayName(XmlUtils.getStringElt(obj, "responsible-party-name"))
      looser.setLoginName(looser.getDisplayName)
      looser.setId(XmlUtils.getIntElt(obj, "responsible-party-id"))
      result.setValue(Assignee, looser)
    }
    result
  }

}
