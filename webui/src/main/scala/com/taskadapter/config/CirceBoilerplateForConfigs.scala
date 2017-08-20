package com.taskadapter.config

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.FieldMapping
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object CirceBoilerplateForConfigs {
  // circe does not support nested Case Classes, thus - boilerplate.
  implicit val fieldDecoder: Decoder[Field] = deriveDecoder[Field]
  implicit val fieldEncoder: Encoder[Field] = deriveEncoder[Field]
  implicit val fooDecoder: Decoder[FieldMapping] = deriveDecoder[FieldMapping]
  implicit val fooEncoder: Encoder[FieldMapping] = deriveEncoder[FieldMapping]
}
