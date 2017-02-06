package com.agoda.filedownloader.streaming

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

  private def find(protocol: String): util.ArrayList[String] = {
    val paths = System.getProperty("java.class.path").split(":")
    val protocolAlias = if (protocol.equalsIgnoreCase("https")) "http" else protocol
    var list = new util.ArrayList[String]();
    paths.foreach { path =>
      logger.debug(path)
      list.addAll(find(protocolAlias, path))
    }
    list
  }

  private def find(protocol: String, path: String): util.ArrayList[String] = {
    val directory = new File(path)
    var list = new util.ArrayList[String]();
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
              list.add(classPath)
            }
          }
        }
        else {
          val dir = directory + File.separator + file
          list.addAll(find(protocol, dir))
        }
      }
    }
    else if (path.endsWith(".jar")) {
      list.addAll(scanJars(path, protocol))
    }
    return list
  }

  private def scanJars(jarPath: String, protocol: String): util.ArrayList[String] = {
    val classNames = new util.ArrayList[String]();
    val zip = new ZipInputStream(new FileInputStream(jarPath));
    var entry: ZipEntry = zip.getNextEntry
    while (entry != null) {
      val entryPath = entry.getName
      if (!entry.isDirectory() && entryPath.endsWith(".class") && entryPath.substring(entryPath.lastIndexOf("/") + 1).equalsIgnoreCase(protocol + pattern)) {
        val className = entryPath.replace('/', '.');
        val classPath = className.substring(0, className.length() - ".class".length())
        if (classOf[ConnectionStream].isAssignableFrom(Class.forName(classPath)))
          classNames.add(classPath);
      }
      entry = zip.getNextEntry
    }
    classNames
  }

  def getStream(protocol: String) = {
    val list = find(protocol);
    if (list.size() == 0)
      throw new ClassNotFoundException("No implementation found for protocol".concat(protocol) + ",Implement com.agoda.ConnectionStream")
    else
      Class.forName(list.get(0)).newInstance().asInstanceOf[ConnectionStream];
  }
}
