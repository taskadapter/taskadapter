package com.taskadapter.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.Field
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object CirceBoilerplateForConfigs {
  // circe does not support nested Case Classes, thus - boilerplate.
//  implicit val fieldDecoder: Decoder[Field[_]] = deriveDecoder[Field[_]]
//  implicit val fieldEncoder: Encoder[Field[_]] = deriveEncoder[Field[_]]
//  implicit val fooDecoder: Decoder[FieldMapping[_]] = deriveDecoder[FieldMapping[_]]
//  implicit val fooEncoder: Encoder[FieldMapping[_]] = deriveEncoder[FieldMapping[_]]
}
