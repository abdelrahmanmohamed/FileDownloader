package com.agoda.downloader.streaming

import java.io.InputStream

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
trait ConnectionStream {
  def openConnection(fileURL: String)

  def getOpenInputStream: InputStream

  def exists(): Boolean

  def getFileName: String

  def getContentLength: Long

  def closeConnection()
}
