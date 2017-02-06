package com.agoda.downloader.managers

import java.io.{File, FileOutputStream, IOException, InputStream}
import java.nio.file.{FileAlreadyExistsException, NoSuchFileException, NotDirectoryException}

import com.agoda.downloader.streaming.StreamProvider
import org.slf4j.LoggerFactory

/**
  * Created by Abdelrahman Mohamed Sayed on 1/26/17.
  * FileDownloader implementation definition to download files from http, https and ftp
  * uses http basic authentication only
  */
class FileDownloader {
  val logger = LoggerFactory.getLogger(getClass)

  /**
    * download file from remote with the provided name
    *
    * remoteFileURL     remote file url
    *
    * @param fileURL           file url
    * @param downloadDirectory download directory
    * @param localFileName     new file name in the download directory
    * @param overwrite         overwrite file if exist in local directory
    * @param bufferSize        buffer size
    * @return true if downloaded successfully
    * @throws NotDirectoryException if download directory doesn't exist or can't be created it
    * @throws NoSuchFileException   if file doesn't exist in remote
    **/

  def downloadFile(fileURL: String, downloadDirectory: String, localFileName: String, overwrite: Boolean, bufferSize: Int): Boolean = {
    if (bufferSize < 1)
      throw new IllegalArgumentException("buffer size should be greater than 0")
    val connectionStream = StreamProvider.getStream(fileURL.substring(0, fileURL.indexOf("://")))
    connectionStream.openConnection(fileURL)
    val downloaded = if (connectionStream.exists()) {
      val inputStream = connectionStream.getOpenInputStream
      val fileName: String = if (localFileName == null) {
        connectionStream.getFileName
      } else {
        localFileName
      }
      saveStreamToFile(inputStream, connectionStream.getContentLength, downloadDirectory, fileName, overwrite, bufferSize)
    } else {
      throw new NoSuchFileException("File doesn't exist in remote")
    }
    connectionStream.closeConnection()
    downloaded
  }

  private def saveStreamToFile(inputStream: InputStream, fileSize: Long, downloadDirectory: String, localFileName: String, overwrite: Boolean, bufferSize: Int): Boolean = {
    val outDir = new File(downloadDirectory)
    if (outDir.exists() || outDir.mkdirs()) {
      val outFile = new File(downloadDirectory + File.separator + localFileName)
      var readCount = 0
      if (outFile.exists() && !overwrite) {
        false
      } else if (outFile.exists() || outFile.createNewFile()) {
        try {
          val outputStream = new FileOutputStream(outFile)
          var bytesRead = -1
          val buffer = new Array[Byte](bufferSize)
          while ((fileSize != -1 && readCount < fileSize) || inputStream.available() > 0) {
            bytesRead = inputStream.read(buffer)
            outputStream.write(buffer, 0, bytesRead)
            outputStream.flush()
            readCount += bytesRead
          }
          outputStream.close()
          inputStream.close()
          if (fileSize != -1 && fileSize != readCount)
            throw new IOException("Connection lost while downloading from remote,original file size:" + fileSize + ",downloaded:" + readCount)
          else
            true
        } catch {
          case ex: Exception =>
            outFile.delete()
            throw new IOException("Connection lost while downloading from remote,original file size:" + fileSize + ",downloaded:" + readCount)
        }
      } else {
        outFile.delete()
        throw new FileAlreadyExistsException("there a file with the same name in download directory")
      }
    } else {
      throw new NotDirectoryException("download directory doesn't exist or can't be created it ")
    }
  }
}
