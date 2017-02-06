package com.agoda.downloader.streaming

import java.io.InputStream
import java.net._
import java.util.Base64

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
class HttpConnectionStream() extends ConnectionStream {
  private var uRLConnection: HttpURLConnection = _
  private var url: URL = _
  private var fileURL: String = _

  def openConnection(fileURL: String): Unit = {
    url = new URL(fileURL)
    this.fileURL = fileURL
    val userInfo = if (url.getUserInfo != null) url.getUserInfo.split(":") else null
    val password: String = if (userInfo != null && userInfo.length > 1) userInfo(1) else null
    val username: String = if (userInfo != null && userInfo.nonEmpty) userInfo(0) else null
    uRLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
    if (username != null && password != null) {
      val userpass = username + ":" + new String(password);
      val basicAuth = "Basic " + new String(Base64.getEncoder.encode(userpass.getBytes()));
      uRLConnection.setRequestProperty("Authorization", basicAuth);
    }
  }

  def exists(): Boolean = {
    uRLConnection.getResponseCode == 200
  }

  def getOpenInputStream: InputStream = {
    uRLConnection.getInputStream
  }

  def closeConnection(): Unit = {
    uRLConnection.disconnect()
  }

  override def getFileName: String = {
    val disposition = uRLConnection.getHeaderField("Content-Disposition")
    val index = if (disposition != null) disposition.indexOf("filename=") else -1
    if (disposition != null && index > 0) {
      disposition.substring(index + 10, disposition.length() - 1)
    } else {
      fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length())
    }
  }

  override def getContentLength: Long = {
    uRLConnection.getContentLengthLong
  }
}
