package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.StandardField

import scala.collection.JavaConverters._

object NewConfigSuggester {
  val DEFAULT_VALUE_FOR_EMPTY_VALUES = ""

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def suggestedFieldMappingsForNewConfig(connector1Combinations: Map[Field, StandardField],
                                         connector2Combinations: Map[Field, StandardField]): List[FieldMapping] = {
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

    val result = scala.collection.mutable.ListBuffer[FieldMapping]()
    connector1Combinations.values.foreach{ standardField =>
      val field1 : Field = connector1Combinations.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
      val field2 : Field = connector2Combinations.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))

      result += FieldMapping(field1, field2, selectByDefault, "")
    }


    result.toList
  }
}
