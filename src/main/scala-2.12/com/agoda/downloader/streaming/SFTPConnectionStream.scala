package com.agoda.filedownloader.streaming

import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.util.Collections

import org.apache.sshd.client.SshClient
import org.apache.sshd.client.subsystem.sftp.{SftpFileSystemProvider, SftpPath}

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
class SFTPConnectionStream() extends ConnectionStream {
  var client: SshClient = _
  var remotePath: SftpPath = _
  private var fileURL: String = _

  override def openConnection(fileURL: String): Unit = {
    this.fileURL = fileURL
    val url = new URL(fileURL.replace("SFTP", "ftp").replace("sftp", "ftp"))
    val userInfo = if (url.getUserInfo != null) url.getUserInfo.split(":") else null
    val password: String = if (userInfo != null && userInfo.length > 1) userInfo(1) else null
    val username: String = if (userInfo != null && userInfo.nonEmpty) userInfo(0) else null
    val host: String = url.getHost
    val port: Int = url.getPort
    client = SshClient.setUpDefaultClient()
    client.start()
    val provider = new SftpFileSystemProvider(client)
    val uri = SftpFileSystemProvider.createFileSystemURI(host, port, username, password)
    val fs = provider.newFileSystem(uri, Collections.emptyMap[String, Object]())
    remotePath = fs.getPath(url.getPath)
  }

  override def getOpenInputStream: InputStream = {
    Files.newInputStream(remotePath)
  }

  override def exists(): Boolean = {
    client.isOpen && Files.exists(remotePath)
  }

  override def closeConnection(): Unit = {
    client.stop()
  }

  override def getFileName: String = {
    remotePath.getFileName.toString
  }
}
