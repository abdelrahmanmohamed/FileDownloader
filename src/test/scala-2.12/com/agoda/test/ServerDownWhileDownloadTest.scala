package com.agoda.test

import java.io.{File, IOException}
import java.net.{Authenticator, PasswordAuthentication}
import java.nio.file.NoSuchFileException

import com.agoda.Utils.FileUtils
import com.agoda.downloader.managers.FileDownloader
import com.agoda.server.SimpleHttpFileServer
import org.junit.Assert._
import org.scalatest._

/**
  * Created by hhmx3422 on 2/6/17.
  */
class ServerDownWhileDownloadTest extends FlatSpec with BeforeAndAfter {
  var server: SimpleHttpFileServer = _
  val path = new File("")
  before {
    FileUtils.delete(new File("download"))
    server = new SimpleHttpFileServer(0, false)
  }

  "Download file through http" should "should be fail server down while download" in {
    val fileDownloader = new FileDownloader()
    val expectedFile = new File("download/big.txt")
    new Thread(() => {
      assertThrows[IOException] {
        fileDownloader.downloadFile("http://localhost:" + server.getPort + "/big.txt", path.getCanonicalPath + File.separator + "download", "big.txt", overwrite = true, 1)
      }
    }
    ).start()
    while (!expectedFile.exists()) {}
    server.stop()
    while (expectedFile.exists()) {}
    assertFalse(expectedFile.exists())
  }
}
