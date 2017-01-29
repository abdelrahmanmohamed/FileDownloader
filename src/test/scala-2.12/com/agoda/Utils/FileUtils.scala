package com.agoda.Utils

import java.io.{File, FileInputStream}

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */
object FileUtils {
  def delete(file: File) {

    if (file.isDirectory) {
      //directory is empty, then delete it
      if (file.list().length == 0) {
        file.delete()
      } else {
        //list all the directory contents
        val files = file.list()
        for (temp: String <- files) {
          //construct the file structure
          val fileToDelete = new File(file, temp)
          //recursive delete
          delete(fileToDelete)
        }
        //check the directory again, if empty then delete it
        if (file.list().length == 0) {
          file.delete()
        }
      }
    } else {
      //if file, then delete it
      file.delete()
    }
  }

  def compareFiles(fileActual: File, fileExpected: File): Boolean = {
    val fisActual = new FileInputStream(fileActual)
    val fisExpected = new FileInputStream(fileExpected)
    if (fisActual.available() != fisExpected.available())
      false
    else {
      while (fisActual.available() > 0) {
        if (fisActual.read() != fisExpected.read())
          return false
      }
      true
    }
  }
}
