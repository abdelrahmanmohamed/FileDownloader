package com.agoda.downloader.managers

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
      val url = inScanner.nextLine()
      executor.execute(new DownloadTask(url, downloadDir, null, overwrite, bufferSize))
    }
    inScanner.close()
    executor.shutdown()
    executor.awaitTermination(Long.MaxValue, TimeUnit.HOURS)
    logger.info("done")
  }
}
