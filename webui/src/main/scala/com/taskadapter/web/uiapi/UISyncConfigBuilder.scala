package com.taskadapter.web.uiapi

import com.taskadapter.config.CirceBoilerplateForConfigs._
import com.taskadapter.config.StoredExportConfig
import com.taskadapter.connector.definition.FieldMapping
import io.circe.parser._

import scala.collection.JavaConverters._

class UISyncConfigBuilder(val uiConfigService: UIConfigService) {
  /**
    * Create a new UI config instance for a stored config.
    *
    * @param ownerName    name of config owner.
    * @param storedConfig stored config to create an instance for.
    * @return new parsed config.
    */
  def uize(ownerName: String, storedConfig: StoredExportConfig): UISyncConfig = {
    val label = storedConfig.getName
    val conn1Config = storedConfig.getConnector1
    val conn2Config = storedConfig.getConnector2
    val config1 = uiConfigService.createRichConfig(conn1Config.getConnectorTypeId, conn1Config.getSerializedConfig)
    val config2 = uiConfigService.createRichConfig(conn2Config.getConnectorTypeId, conn2Config.getSerializedConfig)
    val jsonString = storedConfig.getMappingsString

    val newMappings = decode[Seq[FieldMapping]](jsonString)
    newMappings match {
      case Left(e) => throw new RuntimeException(s"cannot parse mappings from config: $e")
      case Right(m) =>
        new UISyncConfig(storedConfig.getId, ownerName, label, config1, config2, m.asJava, false)
    }
  }
}
