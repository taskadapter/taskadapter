package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.FieldMapping

import scala.collection.JavaConverters._

object NewMappingBuilder {
  val DEFAULT_VALUE_FOR_EMPTY_VALUES = ""

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def suggestedFieldMappingsForNewConfig(config1: UIConnectorConfig, config2: UIConnectorConfig): util.List[FieldMapping] = {
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

    val connector1Combinations = config1.getSuggestedCombinations
    val connector2Combinations = config2.getSuggestedCombinations

    val result = scala.collection.mutable.ListBuffer[FieldMapping]()
    connector1Combinations.values.foreach{ standardField =>
      val field1 : Field = connector1Combinations.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
      val field2 : Field = connector2Combinations.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))

      result += FieldMapping(field1, field2, selectByDefault, "")
    }


    result.asJava
  }
}
