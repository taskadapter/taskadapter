package com.taskadapter.reporting

import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StacktraceCleanerTest extends FunSpec with Matchers {

  it("removed java internals") {
    val value =
      """
        |        at com.atlassian.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient.runInContext(SettableFuturePromiseHttpPromiseAsyncClient.java:90)
        |        at com.atlassian.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient$ThreadLocalDelegateRunnable.run(SettableFuturePromiseHttpPromiseAsyncClient.java:192)
        |        at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
        |        at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
        |        ... 1 more
      """.stripMargin

    StacktraceCleaner.stripInternalStacktraceItems(value) shouldBe
      """
        |        at com.atlassian.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient.runInContext(SettableFuturePromiseHttpPromiseAsyncClient.java:90)
        |        at com.atlassian.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient$ThreadLocalDelegateRunnable.run(SettableFuturePromiseHttpPromiseAsyncClient.java:192)
        |/./
        |/./
        |        ... 1 more
      """.stripMargin
  }

  it("removes java Thread") {
    val value =
      """
        |        at com.taskadapter.connector.common.SimpleTaskSaver.$anonfun$saveTasks$1(SimpleTaskSaver.scala:29)
        |        at java.util.ArrayList.forEach(Unknown Source)
        |        at com.taskadapter.webui.pages.ExportHelper.$anonfun$performExport$1(ExportHelper.scala:88)
        |        at java.lang.Thread.run(Unknown Source)""".stripMargin

    StacktraceCleaner.stripInternalStacktraceItems(value) shouldBe
      """
        |        at com.taskadapter.connector.common.SimpleTaskSaver.$anonfun$saveTasks$1(SimpleTaskSaver.scala:29)
        |        at java.util.ArrayList.forEach(Unknown Source)
        |        at com.taskadapter.webui.pages.ExportHelper.$anonfun$performExport$1(ExportHelper.scala:88)
        |/./""".stripMargin

  }
  it("does not fail with empty input") {
    StacktraceCleaner.stripInternalStacktraceItems("") shouldBe ""
  }
}
