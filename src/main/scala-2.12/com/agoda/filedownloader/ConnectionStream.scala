package com.agoda.filedownloader

import java.io.InputStream

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
trait ConnectionStream {
  def openConnection()

  def getOpenInputStream: InputStream

  def exists(): Boolean

  def getFileName:String

  def closeConnection()
}
