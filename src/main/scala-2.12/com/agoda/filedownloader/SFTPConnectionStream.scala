package com.agoda.filedownloader

import java.io.InputStream
import java.nio.file.Files
import java.util.Collections

import org.apache.sshd.client.SshClient
import org.apache.sshd.client.subsystem.sftp.{SftpFileSystemProvider, SftpPath}

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
class SFTPConnectionStream(userName: String, password: String, host: String, port: Int, filePath: String) extends ConnectionStream {
  var client: SshClient = _
  var remotePath: SftpPath = _

  override def openConnection(): Unit = {
    client = SshClient.setUpDefaultClient()
    client.start()
    val provider = new SftpFileSystemProvider(client)
    val uri = SftpFileSystemProvider.createFileSystemURI(host, port, userName, password)
    val fs = provider.newFileSystem(uri, Collections.emptyMap[String, Object]())
    remotePath = fs.getPath(filePath)
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
