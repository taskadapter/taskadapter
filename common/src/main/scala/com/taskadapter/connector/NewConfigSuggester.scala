package com.taskadapter.connector

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.Field

/**
  * Given two maps of available connector-specific fields (for connector 1 and connector 2),
  * build a list of [[FieldMapping]] with suggested mappings: what fields in connector 1
  * are equivalent to fields in connector 2.
  * Equivalent means they refer to the same [[Field]].
  */
object NewConfigSuggester {

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def suggestedFieldMappingsForNewConfig(list1: Seq[Field[_]], list2: Seq[Field[_]]): Seq[FieldMapping[_]] = {
//    val result = scala.collection.mutable.ListBuffer[FieldMapping[_]]()
//    list1.foreach{ standardField =>
//      val field1 : Field = list1.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
//      val field2 : Field = map2.find(i => i._2 == standardField).map(e => e._1).getOrElse(Field(""))
//      val selected = field1.name != "" && field2.name!= ""
//      result += FieldMapping(field1, field2, selected, "")
//    }
//    val remainingConnector2Fields = map2.keys.filter(f=> !result.exists(e => e.fieldInConnector2.contains(f)))
//
//    remainingConnector2Fields.foreach{ field2 =>
//      val standardFor2 = map2(field2)
//      val field1 : Field[_] = list1.find(i => i._2 == standardFor2).map(e => e._1).getOrElse(Field(""))
//      val selected = field1.name != "" && field2.name!= ""
//      result += FieldMapping(field1, field2, selected, "")
//    }
    list1.intersect(list2).map(f => getMapping(f))
  }

  private def getMapping[T](field: Field[T]) : FieldMapping[T] = {
    FieldMapping.apply(field, field, true, null.asInstanceOf[String])
  }
}
