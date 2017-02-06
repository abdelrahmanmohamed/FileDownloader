package com.agoda.downloader.managers

import org.slf4j.LoggerFactory

/**
  * Created by hhmx3422 on 2/6/17.
  */
class DownloadTask(fileURL: String, downloadDirectory: String, localFileName: String, overwrite: Boolean, bufferSize: Int) extends Runnable {
  val logger = LoggerFactory.getLogger(getClass)

  override def run(): Unit = {
    try {
      logger.info("Downloading : {}", fileURL)
      val d = new FileDownloader()
      d.downloadFile(fileURL, downloadDirectory, null, overwrite, bufferSize)
      logger.info("Done downloading: {}", fileURL)
    } catch {
      case _: Exception => logger.info("Can't download:{}", fileURL)
    }
  }
}
