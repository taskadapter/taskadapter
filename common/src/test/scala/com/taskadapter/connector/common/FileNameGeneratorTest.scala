package com.taskadapter.connector.common

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FileNameGeneratorTest extends FunSpec with Matchers with TempFolder {
  it("file name is incremented when a file already exists") {
    withTempFolder {folder =>
      val filePattern = "file_%d.txt"
      val file = FileNameGenerator.createSafeAvailableFile(folder, filePattern)
      file.getName shouldBe "file_1.txt"
      file.createNewFile()

      val file2 = FileNameGenerator.createSafeAvailableFile(folder, filePattern)
      file2.getName shouldBe "file_2.txt"
    }
  }
}
