package com.agoda.test

import com.agoda.downloader.streaming.{FTPConnectionStream, HttpConnectionStream, SFTPConnectionStream, StreamProvider}
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
  * Created by hhmx3422 on 2/6/17.
  */
class StreamProviderTest extends FlatSpec {
  "Stream Provider" should "provide Stream for http\n" in {
    assert(StreamProvider.getStream("Http").isInstanceOf[HttpConnectionStream])
  }
  it should "provide Stream for https\n" in {
    assert(StreamProvider.getStream("Https").isInstanceOf[HttpConnectionStream])
  }
  it should "provide Stream for ftp\n" in {
    assert(StreamProvider.getStream("ftp").isInstanceOf[FTPConnectionStream])
  }
  it should "provide Stream for sftp\n" in {
    assert(StreamProvider.getStream("sftp").isInstanceOf[SFTPConnectionStream])
  }
}
