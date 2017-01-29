package com.agoda.filedownloader

import java.io.InputStream
import java.net.{Authenticator, HttpURLConnection, URL, URLConnection}

import sun.net.www.protocol.ftp.FtpURLConnection

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
class DefaultConnectionStream(authenticator: Authenticator, fileURL: String) extends ConnectionStream {
  private var uRLConnection: HttpURLConnection = _
  private var url: URL = _

  def this(fileURL: String) = this(null, fileURL)

  def openConnection(): Unit = {
    Authenticator.setDefault(authenticator)
    url = new URL(fileURL)
    uRLConnection = url.openConnection().asInstanceOf[HttpURLConnection]
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
}
