package com.taskadapter.connector.common

import com.taskadapter.connector.definition.{ExportDirection, FieldMapping}
import com.taskadapter.connector.{Field, MappingBuilder}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class MappingBuilderTest extends FunSpec with Matchers {
  it("skips fields that are not selected") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "summary"), new Field("String", "summary"), false, "default")),
      ExportDirection.RIGHT

    )
    rows shouldBe empty
  }

  it("export Right processes selected fields") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "JiraSummary"), new Field("String", "RedmineSummary"), true, "default")),
      ExportDirection.RIGHT

    )
    rows.head.sourceField.get.name shouldBe "JiraSummary"
    rows.head.targetField.get.name shouldBe "RedmineSummary"
  }

  it("export Left processes selected fields") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "JiraSummary"), new Field("String", "RedmineSummary"), true, "default")),
      ExportDirection.LEFT

    )
    rows.head.sourceField.get.name shouldBe "RedmineSummary"
    rows.head.targetField.get.name shouldBe "JiraSummary"
  }

}
