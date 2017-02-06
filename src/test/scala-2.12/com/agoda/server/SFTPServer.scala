package com.agoda.server

import java.io.File
import java.util

import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.scp.ScpCommandFactory
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import org.apache.sshd.server.{Command, CommandFactory, SshServer}

/**
  * Created by Abdelrahman Sayed on 1/28/17.
  */
class SFTPServer(userName: String, password: String, port: Int) {
  var sshd: SshServer = _

  def start(): Unit = {
    sshd = SshServer.setUpDefaultServer()
    sshd.setPort(port)
    val provider = new SimpleGeneratorHostKeyProvider()
    sshd.setKeyPairProvider(provider)
    sshd.setPasswordAuthenticator((_: String, _: String, _: ServerSession) => {
      true
    })
    val myCommandFactory = new CommandFactory() {
      override def createCommand(command: String): Command = {
        null
      }
    }
    val factory = new ScpCommandFactory()
    factory.setDelegateCommandFactory(myCommandFactory)
    sshd.setCommandFactory(factory)
    val sftpSubsystemFactory = new SftpSubsystemFactory()
    val namedFactoryList = new util.ArrayList[NamedFactory[Command]]()
    namedFactoryList.add(sftpSubsystemFactory)
    sshd.setSubsystemFactories(namedFactoryList)
    val fsFactory = new VirtualFileSystemFactory()
    fsFactory.setUserHomeDir(userName, new File("").getAbsoluteFile.toPath)
    sshd.setFileSystemFactory(fsFactory)
    sshd.start()
  }

  def getPort: Int = {
    sshd.getPort
  }

  def stop(): Unit = {
    sshd.stop()
  }
}

