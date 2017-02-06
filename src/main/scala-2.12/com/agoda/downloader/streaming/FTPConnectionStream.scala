package com.agoda.downloader.streaming

import java.io.{FileNotFoundException, InputStream}
import java.net._
import java.util.Base64

import sun.net.www.protocol.ftp.FtpURLConnection

/**
  * Created by Abdelrahman Sayed on 1/29/17.
  */
class FTPConnectionStream() extends ConnectionStream {
  private var uRLConnection: FtpURLConnection = _
  private var url: URL = _
  private var fileURL: String = _

  def openConnection(fileURL: String): Unit = {
    this.fileURL = fileURL
    val url = new URL(fileURL)
    val userInfo = if (url.getUserInfo != null) url.getUserInfo.split(":") else null
    val password: String = if (userInfo != null && userInfo.length > 1) userInfo(1) else null
    val username: String = if (userInfo != null && userInfo.nonEmpty) userInfo(0) else null
    uRLConnection = url.openConnection().asInstanceOf[FtpURLConnection]
    if (username != null && password != null) {
      val userpass = username + ":" + new String(password);
      val basicAuth = "Basic " + new String(Base64.getEncoder.encode(userpass.getBytes()));
      uRLConnection.setRequestProperty("Authorization", basicAuth);
    }
  }

  def exists(): Boolean = {
    try {
      uRLConnection.getInputStream
      true
    } catch {
      case _: FileNotFoundException =>
        false
    }
  }

  def getOpenInputStream: InputStream = {
    uRLConnection.getInputStream
  }

  def closeConnection(): Unit = {
    uRLConnection.close()
  }

  override def getFileName: String = {
    fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length())
  }

  override def getContentLength: Long = {
    uRLConnection.getContentLengthLong()
  }
}

