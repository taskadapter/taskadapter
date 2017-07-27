package com.taskadapter.webui

import org.slf4j.LoggerFactory

/**
  * Sample usage for Scala:
  * {{{
  * val log = TALog.log
  * }}}
  *
  * For Java:
  * {{{
  * private Logger log = TALog.log();
  * }}}
  */
object TALog {
  val log = LoggerFactory.getLogger(TALog.getClass)
}
