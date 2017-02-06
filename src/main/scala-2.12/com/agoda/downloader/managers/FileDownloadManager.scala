package com.agoda.filedownloader.downloaders

import java.io.File
import java.util.Scanner
import java.util.concurrent.{Executors, TimeUnit}

import org.slf4j.LoggerFactory

/**
  * Created by hhmx3422 on 2/6/17.
  */
class FileDownloadManager(val inputFileName: String, val downloadDir: String,
                          val bufferSize: Int, val overwrite: Boolean,
                          val threadPollSize: Int) {
  val logger = LoggerFactory.getLogger(getClass)
  val executor = Executors.newFixedThreadPool(threadPollSize)
  def download = {
    logger.info("input File : {}", inputFileName)
    logger.info("output directory : {}", downloadDir)
    val inFile = new File(inputFileName)
    val inScanner = new Scanner(inFile)
    while (inScanner.hasNext) {
      try {
        val url = inScanner.nextLine()
        logger.info("Downloading : {}", url)
        executor.execute(() => {
          try {
            val d = new FileDownloader()
            d.downloadFile(url, downloadDir, null, overwrite, bufferSize)
            logger.info("Done downloading: {}", url)
          } catch {
            case _: Exception => logger.info("Can't download:{}", url)
          }
        })
      } catch {
        case ex: Exception =>
          throw ex
      }
    }
    inScanner.close()
    executor.shutdown()
    executor.awaitTermination(Long.MaxValue, TimeUnit.HOURS)
    logger.info("done")
  }
}
