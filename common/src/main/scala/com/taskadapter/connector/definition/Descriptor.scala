package com.taskadapter.connector.definition

/**
  * @param id    the Connector id. Once defined in a connector, this id should not be changed in the connector
  *              to avoid breaking compatibility.
  * @param label user-friendly label to show on "New Config" page. This can be freely changed in a new version of a
  *              connector
  */
case class Descriptor(id: String, label: String)