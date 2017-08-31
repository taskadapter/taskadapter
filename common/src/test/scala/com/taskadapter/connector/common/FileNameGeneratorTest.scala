package com.taskadapter.connector.common

import com.taskadapter.connector.testlib.TempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FileNameGeneratorTest extends FunSpec with Matchers with TempFolder {
  it("second file name is different from first when a file already exists") {
    withTempFolder { folder =>
      val filePattern = "file_%d.txt"
      val file = FileNameGenerator.createSafeAvailableFile(folder, filePattern)
      file.createNewFile()

      val file2 = FileNameGenerator.createSafeAvailableFile(folder, filePattern)
      file2.getName should not be file.getName
    }
  }
}
