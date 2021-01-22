package com.taskadapter.webui.results

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.DateUtils
import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.uiapi.ConfigsTempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

@RunWith(classOf[JUnitRunner])
class ExportResultStorageTest extends FunSpec with Matchers with ConfigsTempFolder {
  it("can save and load results with errors") {
    withTempFolder { folder =>
      val storage = new ExportResultStorage(folder, 10)
      val result = ExportResultFormat("1", ConfigId("admin", 1),
        "label1", "from", "to", None, 1, 1, Seq("some general error"),
        Seq((TaskId(100, "KEY100"), "error summary", "detailed error")), DateUtils.getDateRoundedToMinutes, 100)
      storage.store(result)

      storage.getSaveResults().head shouldBe result
    }
  }
}
