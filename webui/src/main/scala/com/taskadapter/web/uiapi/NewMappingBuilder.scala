package com.taskadapter.web.uiapi

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.{FieldMapping, NewMappings}
import com.taskadapter.model.GTaskDescriptor

import scala.collection.JavaConverters._
import java.util
import java.util.List

object NewMappingBuilder {
  val DEFAULT_VALUE_FOR_EMPTY_VALUES = ""

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def createNewMappings(m1: util.List[Field], m2: util.List[Field]): util.List[FieldMapping] = {
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
    val selectByDefault = true
    val commonFields = m1.asScala.intersect(m2.asScala)
    commonFields.map { field =>
//      if (field.name != GTaskDescriptor.FIELD.ID.name() && field != GTaskDescriptor.FIELD.REMOTE_ID.name()) {
        // TODO TA3 this is wrong. same field is put twice. need to put separately for connector 1 and 2
        FieldMapping(field, field, selectByDefault, DEFAULT_VALUE_FOR_EMPTY_VALUES)
//      }
    }.asJava
  }
}
