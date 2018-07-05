package com.taskadapter.connector.common

import java.io.File

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FileNameGeneratorTest extends FunSpec with Matchers {
  it("second file name is different from first when a file already exists") {
    val folder = new File(System.getProperty("java.io.tmpdir"));

    val filePattern = "file_%d.txt"
    val file = FileNameGenerator.findSafeAvailableFileName(folder, filePattern)
    file.createNewFile()
    file.deleteOnExit()

    val file2 = FileNameGenerator.findSafeAvailableFileName(folder, filePattern)
    file2.getName should not be file.getName
    file2.deleteOnExit()
  }
}
