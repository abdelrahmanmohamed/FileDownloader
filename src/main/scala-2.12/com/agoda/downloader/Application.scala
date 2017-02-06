package com.agoda.downloader

import java.io.File
import java.net.{Authenticator, InetAddress, PasswordAuthentication, URL}
import java.util.Scanner
import java.util.concurrent.{Executors, TimeUnit}

import com.agoda.downloader.managers.FileDownloadManager
import org.slf4j.LoggerFactory

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */
object Application extends App {
  val inputFileName = args(0)
  val downloadDir = args(1)
  val fileDownloaderManager = new FileDownloadManager(inputFileName, downloadDir, 1024, true, 10)
  fileDownloaderManager.download
}