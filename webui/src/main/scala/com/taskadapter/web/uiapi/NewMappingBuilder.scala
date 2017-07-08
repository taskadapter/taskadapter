package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.connector.definition.{FieldMapping, NewMappings}
import com.taskadapter.model.GTaskDescriptor

import scala.collection.JavaConverters._

object NewMappingBuilder {
  val DEFAULT_VALUE_FOR_EMPTY_VALUES = ""

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def createNewMappings(m1: util.List[String], m2: util.List[String]): NewMappings = {
    val res = new NewMappings
    // TODO TA3 restore remote ids
    /*
            if (m2.isFieldSupported(GTaskDescriptor.FIELD.REMOTE_ID)) {
                res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null,
                        m2.getDefaultValue(GTaskDescriptor.FIELD.REMOTE_ID),
                        m2.isSelectedByDefault(GTaskDescriptor.FIELD.REMOTE_ID),
                        DEFAULT_VALUE_FOR_EMPTY_VALUES));
            }

            if (m1.isFieldSupported(GTaskDescriptor.FIELD.REMOTE_ID)) {
                res.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID,
                        m1.getDefaultValue(GTaskDescriptor.FIELD.REMOTE_ID), null,
                        m1.isSelectedByDefault(GTaskDescriptor.FIELD.REMOTE_ID),
                        DEFAULT_VALUE_FOR_EMPTY_VALUES));
            }
    */
    val commonFields = m1.asScala.intersect(m2.asScala)
    commonFields.foreach { field =>
      if (field != GTaskDescriptor.FIELD.ID.name() && field != GTaskDescriptor.FIELD.REMOTE_ID.name()) {
        val selectByDefault = true
        res.put(new FieldMapping(field, "", "", selectByDefault, DEFAULT_VALUE_FOR_EMPTY_VALUES))
      }
    }
    res
  }
}
