package com.taskadapter.reporting

/**
  * strips some irrelevant stacktrace elements, like Java internals
  */
object StacktraceCleaner {

  private val strings = Set("java.util.concurrent", "java.lang.Thread")
  private val regexp = strings.map(s => s".*($s).*").mkString("|")

  val replacement = "/./"

  def stripInternalStacktraceItems(stackTrace: String) : String = {
    stackTrace
      .split(System.lineSeparator())
      .map(line =>  {
        val trimmed = line.trim

        if (trimmed.matches(regexp)) {
          replacement
        } else {
          line
        }
      })
      .mkString(System.lineSeparator())
  }

}
