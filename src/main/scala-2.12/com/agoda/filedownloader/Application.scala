package com.agoda.filedownloader

import java.io.File
import java.net.{Authenticator, InetAddress, PasswordAuthentication, URL}
import java.util.Scanner
import java.util.concurrent.{Executors, TimeUnit}

import org.slf4j.LoggerFactory

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */
object Application extends App {
  val logger = LoggerFactory.getLogger(getClass)
  val inputFileName = args(0)
  val downloadDir = args(1)
  logger.info("Input File : {}", inputFileName)
  logger.info("out directory : {}", downloadDir)
  val overwrite = true
  val bufferSize: Int = 1024
  val threadPollSize = 10
  val executor = Executors.newFixedThreadPool(threadPollSize)
  var stream: ConnectionStream = _
  val inFile = new File(inputFileName)
  val inScanner = new Scanner(inFile)
  while (inScanner.hasNext) {
    try {
      val url = inScanner.nextLine()
      val aURL = new URL(url.replace("sftp", "ftp"))
      val userInfo = if (aURL.getUserInfo != null) aURL.getUserInfo.split(":") else null
      val password: String = if (userInfo != null && userInfo.length > 1) userInfo(1) else null
      val username: String = if (userInfo != null && userInfo.nonEmpty) userInfo(0) else null
      val host: String = aURL.getHost
      val protocol: String = if (url.toLowerCase().contains("sftp")) "sftp" else aURL.getProtocol
      val port: Int = aURL.getPort
      val path = aURL.getFile
      logger.info("downloading : {}", url)
      var stream: ConnectionStream = null
      var auth: Authenticator = null
      if (username != null && !username.equals("")) {
        auth = new Authenticator() {
          override def getPasswordAuthentication: PasswordAuthentication = {
            Authenticator.requestPasswordAuthentication(host, InetAddress.getByName(host), port, protocol, null, protocol)
          }
        }
      }
      if (protocol.toLowerCase() == "sftp") {
        stream = new SFTPConnectionStream(username, password, host, port, path)
      } else if (protocol.toLowerCase() == "ftp") {
        stream = new FTPStream(auth, url)
      } else {
        stream = new DefaultConnectionStream(auth, url)
      }
      val d = new FileDownloader()
      executor.execute(() => {
        try {
          d.downloadFile(stream, downloadDir, null, overwrite, bufferSize)
          logger.info("done downloading: {}", url)
        } catch {
          case _: Exception => logger.info("Can't download:{}", url)
        }
      })
    } catch {
      case ex: Exception =>
        throw ex
    }
  }
  executor.shutdown()
  executor.awaitTermination(Long.MaxValue, TimeUnit.HOURS)
  logger.info("done")
}