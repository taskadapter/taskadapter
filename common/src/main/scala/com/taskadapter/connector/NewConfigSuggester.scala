package com.taskadapter.connector

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.StandardField

/**
  * Given two maps of available connector-specific fields (for connector 1 and connector 2),
  * build a list of [[FieldMapping]] with suggested mappings: what fields in connector 1
  * are equivalent to fields in connector 2.
  * Equivalent means they refer to the same [[StandardField]].
  */
object NewConfigSuggester {
  val DEFAULT_VALUE_FOR_EMPTY_VALUES = ""

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def suggestedFieldMappingsForNewConfig(map1: Map[Field, StandardField],
                                         map2: Map[Field, StandardField]): List[FieldMapping] = {
    val result = scala.collection.mutable.ListBuffer[FieldMapping]()

    map1.values.foreach{ standardField =>
      val field1 : Field = map1.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
      val field2 : Field = map2.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
      val selected = field1.name != "" && field2.name!= ""
      result += FieldMapping(field1, field2, selected, "")
    }
    val remainingConnector2Fields = map2.keys.filter(f=> !result.exists(e => e.fieldInConnector2.contains(f)))

    remainingConnector2Fields.foreach{ field2 =>
      val standardFor2 = map2(field2)
      val field1 : Field = map1.find(i => i._2 == standardFor2).map(e => e._1).getOrElse(Field(""))
      val selected = field1.name != "" && field2.name!= ""
      result += FieldMapping(field1, field2, selected, "")
    }

    result.toList
  }
}
