package com.taskadapter.web.uiapi

/**
  * Class to uniquely identify a config in the store
  *
  * @param ownerName login name of the owner
  * @param id        string-based identifier. this is currently equal to file name where config is stored.
  */
case class ConfigId(ownerName: String, id: String)
