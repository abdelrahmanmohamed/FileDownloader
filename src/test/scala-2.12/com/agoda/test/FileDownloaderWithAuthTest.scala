package com.agoda.test

import java.io.File
import java.net.{Authenticator, PasswordAuthentication}
import java.nio.file.NoSuchFileException

import com.agoda.Utils.FileUtils
import com.agoda.downloader.managers.FileDownloader
import com.agoda.server.SimpleHttpFileServer
import org.junit.Assert.assertTrue
import org.scalatest._

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */
class FileDownloaderWithAuthTest extends FlatSpec with BeforeAndAfter {

  var server: SimpleHttpFileServer = _
  val path = new File("")
  val sampleFile = new File(path.getCanonicalPath + "/samples/s1.txt")
  val auth = new Authenticator() {
    override def getPasswordAuthentication: PasswordAuthentication = {
      new PasswordAuthentication("test", "test".toCharArray)
    }
  }
  before {
    FileUtils.delete(new File("download"))
    server = new SimpleHttpFileServer(0, true)
  }

  "Download file through http with auth" should "should be downloaded successfully\n" in {
    val fileDownloader = new FileDownloader()
    val path = new File("")
    assertTrue(fileDownloader.downloadFile("http://test:test@localhost:" + server.getPort + "/s1.txt", path.getCanonicalPath +File.separator+ "download", "s12.txt", overwrite = true, 1))
    val file = new File(path.getCanonicalPath + "/download/s12.txt")
    FileUtils.compareFiles(file, new File(path.getCanonicalPath + "/samples/s1.txt"))
  }

  it should "should be downloaded successfully with different bufferSize\n" in {
    val fileDownloader = new FileDownloader()
    val path = new File("")
    assertTrue(fileDownloader.downloadFile("http://test:test@localhost:" + server.getPort + "/s1.txt", path.getCanonicalPath +File.separator+ "download", "s13.txt", overwrite = true, 1024))
    val file = new File(path.getCanonicalPath + "/download/s13.txt")
    FileUtils.compareFiles(file, new File(path.getCanonicalPath + "/samples/s1.txt"))
  }

  it should "should be fail illegal argument,buffer size should be > 0" in {
    val fileDownloader = new FileDownloader()
    val path = new File("")
    assertThrows[IllegalArgumentException] {
      fileDownloader.downloadFile("http://test:test@localhost:" + server.getPort + "/s1.txt", path.getCanonicalPath +File.separator+ "download", "s4.txt", overwrite = true, 0)
    }
  }

  it should "should be fail file not found\n" in {
    val fileDownloader = new FileDownloader()
    val path = new File("")
    assertThrows[NoSuchFileException] {
      fileDownloader.downloadFile("http://test:test@localhost:" + server.getPort + "/s5.txt", path.getCanonicalPath +File.separator+ "download", "s5.txt", overwrite = true, 1024)
    }
  }

  after {
    FileUtils.delete(new File("download"))
    server.stop()
  }
}