package com.agoda.server

import java.io.{BufferedInputStream, File, FileInputStream}
import java.net.InetSocketAddress

import com.sun.net.httpserver._

/**
  * Created by Abdelrahman Sayed on 1/22/17.
  */

class GetHandler extends HttpHandler {
  override def handle(httpExchange: HttpExchange): Unit = {
    ;
    val url = httpExchange.getRequestURI
    val file = new File("samples" + url)
    val os = httpExchange.getResponseBody
    if (file.exists()) {
      val byteArray = new Array[Byte](file.length().asInstanceOf[Int])
      val fileInputStream = new FileInputStream(file)
      val bufferedInputStream = new BufferedInputStream(fileInputStream)
      bufferedInputStream.read(byteArray, 0, byteArray.length)

      // ok, we are ready to send the response.
      httpExchange.sendResponseHeaders(200, file.length())
      os.write(byteArray, 0, byteArray.length)
    } else {
      httpExchange.sendResponseHeaders(404, 0)
    }
    os.close()
  }
}

class SimpleHttpFileServer(port: Int, withAuthentication: Boolean) {
  val server: HttpServer = HttpServer.create(new InetSocketAddress(port), 0)
  val context: HttpContext = server.createContext("/", new GetHandler())
  if (withAuthentication) {
    val authenticator = new BasicAuthenticator("test") {
      override def checkCredentials(user: String, pwd: String): Boolean = {
        user.equals("test") && pwd.equals("test")
      }
    }
    context.setAuthenticator(authenticator)
  }
  server.setExecutor(null); // creates a default executor
  server.start()

  def getPort: Int ={
    server.getAddress.getPort
  }
  def stop(): Unit = {
    server.stop(0)
  }
}
