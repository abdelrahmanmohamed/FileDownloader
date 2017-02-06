package com.agoda.test

import java.io.File

import com.agoda.Utils.FileUtils
import com.agoda.downloader.managers.FileDownloadManager
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * Created by hhmx3422 on 2/6/17.
  */
class FileDownloadManagerTest extends FlatSpec with BeforeAndAfter {
  before {
    FileUtils.delete(new File("download"))
  }
  "File downloader manager" should "downloader the list of files" in {
    val currentPathDir = new File("download")
    val manager = new FileDownloadManager("input.txt", currentPathDir.getCanonicalPath, 1024, true, 10)
    manager.download
    assert(currentPathDir.listFiles().length == 4)
  }
  after {
    FileUtils.delete(new File("download"))
  }
}
