package com.taskadapter.web.uiapi

/**
  * Class to uniquely identify a config in the store
  *
  * @param ownerName login name of the owner
  * @param id        numeric identifier
  */
case class ConfigId(ownerName: String, id: Int)
