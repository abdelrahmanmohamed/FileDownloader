package com.agoda.test


import java.io.File
import java.nio.file.NoSuchFileException

import com.agoda.Utils.FileUtils
import com.agoda.filedownloader.{DefaultConnectionStream, FileDownloader}
import com.agoda.server.SimpleHttpFileServer
import org.junit.Assert.assertTrue
import org.scalatest._

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */
class FileDownloaderTest extends FlatSpec with BeforeAndAfter {

  var server: SimpleHttpFileServer = _
  val path = new File("")
  val sampleFile = new File(path.getCanonicalPath + "/samples/s1.txt")
  before {
    FileUtils.delete(new File("download"))
    server = new SimpleHttpFileServer(0, false)
  }

  "Download file through http" should "should be downloaded successfully and name should be as provided\n" in {
    val fileDownloader = new FileDownloader()
    val stream = new DefaultConnectionStream("http://localhost:" + server.getPort + "/s1.txt")
    assertTrue(fileDownloader.downloadFile(stream, path.getCanonicalPath + "/download", "s1.txt", overwrite = true, 1))
    val file = new File(path.getCanonicalPath + "/download/s1.txt")
    FileUtils.compareFiles(file, sampleFile)
  }

  "Download file through http" should "should be downloaded successfully with different bufferSize\n" in {
    val fileDownloader = new FileDownloader()
    val stream = new DefaultConnectionStream("http://localhost:" + server.getPort + "/s1.txt")
    assertTrue(fileDownloader.downloadFile(stream, path.getCanonicalPath + "/download", "s2.txt", overwrite = true, 1024))
    val file = new File(path.getCanonicalPath + "/download/s2.txt")
    FileUtils.compareFiles(file, new File(path.getCanonicalPath + "/samples/s1.txt"))
  }

  "Download file through http" should "should be fail illegal argument,buffer size should be > 0" in {
    val fileDownloader = new FileDownloader()
    val stream = new DefaultConnectionStream("http://localhost:" + server.getPort + "/s1.txt")
    assertThrows[IllegalArgumentException] {
      fileDownloader.downloadFile(stream, path.getCanonicalPath + "/download", "s4.txt", overwrite = true, 0)
    }
  }

  "Download file through http" should "should be fail file not found\n" in {
    val fileDownloader = new FileDownloader()
    val stream = new DefaultConnectionStream("http://localhost:" + server.getPort + "/s4.txt")
    assertThrows[NoSuchFileException] {
      fileDownloader.downloadFile(stream, path.getCanonicalPath + "/download", "s4.txt", overwrite = true, 1024)
    }
  }

  after {
    server.stop()
    FileUtils.delete(new File("download"))
  }
}
