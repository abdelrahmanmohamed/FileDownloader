package com.agoda.filedownloader

import java.io.{FileNotFoundException, InputStream}
import java.net.{Authenticator, HttpURLConnection, URL, URLConnection}

import sun.net.www.protocol.ftp.FtpURLConnection

/**
  * Created by Abdelrahman Sayed on 1/29/17.
  */
class FTPStream(authenticator: Authenticator, fileURL: String) extends ConnectionStream {
  private var uRLConnection: FtpURLConnection = _
  private var url: URL = _

  def this(fileURL: String) = this(null, fileURL)

  def openConnection(): Unit = {
    Authenticator.setDefault(authenticator)
    url = new URL(fileURL)
    uRLConnection = url.openConnection().asInstanceOf[FtpURLConnection]
  }

  def exists(): Boolean = {
    try {
      uRLConnection.getInputStream
      true
    } catch {
      case _:FileNotFoundException =>
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
}

