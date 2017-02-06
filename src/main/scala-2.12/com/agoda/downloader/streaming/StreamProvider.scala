package com.agoda.downloader.streaming

import java.io.{File, FileInputStream}
import java.util
import java.util.zip.{ZipEntry, ZipInputStream}
import javax.swing.JOptionPane

import org.slf4j.LoggerFactory

/**
  * Created by hhmx3422 on 2/5/17.
  */
object StreamProvider {
  val pattern = "ConnectionStream"
  val logger = LoggerFactory.getLogger(getClass)

  private def find(protocol: String): String = {
    val paths = System.getProperty("java.class.path").split(":")
    val protocolAlias = if (protocol.equalsIgnoreCase("https")) "http" else protocol
    paths.foreach { path =>
      logger.debug(path)
      val returnClass = find(protocolAlias, path)
      if (returnClass != null)
        return returnClass
    }
    return null
  }

  private def find(protocol: String, path: String): String = {
    val directory = new File(path)
    if (directory.exists() && directory.isDirectory) {
      val files = directory.list();
      files.foreach { file =>
        if (file.endsWith(".class")) {
          val classname = file.substring(0, file.length() - 6);
          var dir = path
          if (path.contains("classes/"))
            dir = path.substring(path.indexOf("classes/") + 8)
          if (classname.equalsIgnoreCase(protocol + pattern)) {
            val classPath = dir.replace(File.separator, ".") + "." + classname
            if (classOf[ConnectionStream].isAssignableFrom(Class.forName(classPath))) {
              return classPath
            }
          }
        } else {
          val dir = directory + File.separator + file
          val returnedClass = find(protocol, dir)
          if (returnedClass != null)
            return returnedClass
        }
      }
    }
    else if (path.endsWith(".jar")) {
      val returnedClass = scanJars(path, protocol)
      if (returnedClass != null)
        return returnedClass
    }
    return null
  }

  private def scanJars(jarPath: String, protocol: String): String = {
    val zip = new ZipInputStream(new FileInputStream(jarPath));
    var entry: ZipEntry = zip.getNextEntry
    while (entry != null) {
      val entryPath = entry.getName
      if (!entry.isDirectory() && entryPath.endsWith(".class") && entryPath.substring(entryPath.lastIndexOf("/") + 1).equalsIgnoreCase(protocol + pattern)) {
        val className = entryPath.replace('/', '.');
        val classPath = className.substring(0, className.length() - ".class".length())
        if (classOf[ConnectionStream].isAssignableFrom(Class.forName(classPath)))
          return classPath;
      }
      entry = zip.getNextEntry
    }
    return null
  }

  def getStream(protocol: String): ConnectionStream = {
    val returnedClass = find(protocol);
    if (returnedClass == null)
      throw new ClassNotFoundException("No implementation found for protocol".concat(protocol) + ",Implement com.agoda.ConnectionStream")
    else
      return Class.forName(returnedClass).newInstance().asInstanceOf[ConnectionStream];
  }
}
