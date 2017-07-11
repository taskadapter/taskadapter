package com.taskadapter.connector.common

import com.taskadapter.connector.definition.{ExportDirection, FieldMapping}
import com.taskadapter.connector.{Field, MappingBuilder}
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

class MappingBuilderTest extends FunSpec with Matchers {
  it("skips fields that are not selected") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "summary"), new Field("String", "summary"), false, "default")).asJava,
      ExportDirection.RIGHT

    )
    rows.asScala shouldBe empty
  }

  it("export Right processes selected fields") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "JiraSummary"), new Field("String", "RedmineSummary"), true, "default")).asJava,
      ExportDirection.RIGHT

    )
    rows.asScala.head.sourceField.name shouldBe "JiraSummary"
    rows.asScala.head.targetField.name shouldBe "RedmineSummary"
  }

  it("export Left processes selected fields") {
    val rows = MappingBuilder.build(
      List(FieldMapping(new Field("String", "JiraSummary"), new Field("String", "RedmineSummary"), true, "default")).asJava,
      ExportDirection.LEFT

    )
    rows.asScala.head.sourceField.name shouldBe "RedmineSummary"
    rows.asScala.head.targetField.name shouldBe "JiraSummary"
  }

}
