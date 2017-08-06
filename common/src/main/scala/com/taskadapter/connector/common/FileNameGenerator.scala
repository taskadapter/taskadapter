package com.taskadapter.connector.common

import java.io.File

import org.slf4j.LoggerFactory

object FileNameGenerator {
  val logger = LoggerFactory.getLogger(FileNameGenerator.getClass)

  /**
    * Search for an unused file name in the given folder starting with suffix 1.
    *
    * @param rootFolder folder to generate a file name for
    * @param format     sample: `MSP_export_%d.xml`
    * @return
    */
  def createSafeAvailableFile(rootFolder: File, format: String): File = {
    val safeFormat = makeFileNameDiskSafe(format)
    var number = 1
    rootFolder.mkdirs
    while ( {
      number < 10000 // give a chance to exit
    }) {
      val file = new File(rootFolder, String.format(safeFormat, number.asInstanceOf[Integer]))
      logger.info(s"Checking if file name ${file.getAbsolutePath} is available...")
      if (!file.exists) return file
      number += 1
    }
    throw new RuntimeException("cannot generate available file name after many attempts")
  }

  def makeFileNameDiskSafe(potentialFileName: String): String = {
    potentialFileName.replace(" ", "_")
  }
}
