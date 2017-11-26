package com.taskadapter.webui.config

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.FieldMapping
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class EditablePojoMappingsTest extends FunSpec with Matchers {

  val connector1FieldLoader = new ConnectorFieldLoader(List[Field](
    Field("String", "field 1"),
    Field("String", "field 2"),
    Field("String", "summary"),
    Field("String", "another")
  )
    .asJava)
  val connector2FieldLoader = connector1FieldLoader


  it("returns empty field name on the left as None") {
    val mappings = new EditablePojoMappings(Seq(FieldMapping(Some(Field("field 1")), Some(Field("field 2")), true, "default")), connector1FieldLoader, connector2FieldLoader)
    mappings.editablePojoMappings.head.fieldInConnector1 = ""

    mappings.getElements.toSeq shouldBe List(FieldMapping(None, Some(Field("field 2")), true, "default"))
  }

  it("returns empty field name on the right as None") {
    val mappings = new EditablePojoMappings(Seq(FieldMapping(Some(Field("field 1")), Some(Field("field 2")), true, "default")), connector1FieldLoader, connector2FieldLoader)
    mappings.editablePojoMappings.head.fieldInConnector2 = ""

    mappings.getElements.toSeq shouldBe List(FieldMapping(Some(Field("field 1")), None, true, "default"))
  }

  // Vaadin sets NULL as field value when you select an "empty" element in ListSelect
  it("field cleared with null becomes None") {
    val mappings = new EditablePojoMappings(Seq(FieldMapping(Some(Field("field 1")), Some(Field("date 1")), true, "default")), connector1FieldLoader, connector2FieldLoader)
    mappings.editablePojoMappings.head.fieldInConnector2 = null

    mappings.getElements.toSeq shouldBe List(FieldMapping(Some(Field("field 1")), None, true, "default"))
  }

  it("returns new field") {
    val mappings = new EditablePojoMappings(Seq(), connector1FieldLoader, connector2FieldLoader)
    mappings.add(new EditableFieldMapping("123", "", "summary", true, "default"))
    mappings.getElements.toSeq shouldBe List(FieldMapping(None, Some(Field("summary")), true, "default"))
  }

  it("removes empty rows") {
    val mappings = new EditablePojoMappings(Seq(), connector1FieldLoader, connector2FieldLoader)
    mappings.add(new EditableFieldMapping("100", "", "summary", true, "default"))
    mappings.add(new EditableFieldMapping("200", "field 1", "", true, "default"))
    mappings.add(new EditableFieldMapping("300", "", "", true, "default"))
    mappings.add(new EditableFieldMapping("400", "", "another", true, "default"))
    mappings.removeEmptyRows()
    mappings.getElements.toSeq shouldBe
      List(FieldMapping(None, Some(Field("summary")), true, "default"),
        FieldMapping(Some(Field("field 1")), None, true, "default"),
        FieldMapping(None, Some(Field("another")), true, "default"),
      )
  }

}
