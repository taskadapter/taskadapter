package com.taskadapter.web.uiapi

/**
  * Class to uniquely identify a saved setup (like web server url, credentials, ...) in the store
  *
  * @param id string-based identifier. this is currently equal to file name where setup is stored.
  */
case class SetupId(id: String)
