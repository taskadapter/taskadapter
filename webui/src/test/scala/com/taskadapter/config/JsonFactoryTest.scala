package com.taskadapter.config

import com.taskadapter.connector.common.FieldMappingBuilder.getMapping
import com.taskadapter.model.{CustomDate, CustomFloat, CustomString, DoneRatio, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class JsonFactoryTest extends FunSpec with Matchers {

  it("can save and load fields") {
    val fieldMappings = Seq(
      getMapping(Summary),
      getMapping(DoneRatio),
      getMapping(CustomDate("f")),
      getMapping(CustomFloat("f")),
      getMapping(CustomString("f"))
    )
    val str = JsonFactory.toString(fieldMappings.toSeq)
    val parsed = JsonFactory.fromJsonString(str)
    parsed shouldBe fieldMappings
  }
}