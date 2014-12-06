package com.github.jamii.sws

import java.net.URLConnection

import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http._

object Main extends App {

  val documentRoot = sys.props.get("DocumentRoot")
    .getOrElse(throw new IllegalArgumentException("invalid document root"))

  val port = sys.props.get("Listen")
    .flatMap( p => scala.util.control.Exception.allCatch.opt(Integer.parseInt(p)))
    .getOrElse(9000)

  def treatPath(path:String):String = path.replaceAll("""/\.{1,2}/""", "/")

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] =
      Future.value{
        Filer.path2File(documentRoot, treatPath(req.getUri)) match{
          case Some(file) =>
            Filer.file2Buff(file) match{
              case Some(channelBuffer) =>
                val res = new DefaultHttpResponse(req.getProtocolVersion, HttpResponseStatus.NOT_FOUND)
                val contentType = Option(URLConnection.guessContentTypeFromName(file.getName))
                res.setHeader("Content-Type", contentType.getOrElse("text/plain"))
                res.setContent(channelBuffer)
                res
              case None =>
                System.err.println(s"FileNotFound : ${file.getAbsolutePath}")
                new DefaultHttpResponse(req.getProtocolVersion, HttpResponseStatus.OK)
            }
          case None => new DefaultHttpResponse(
            req.getProtocolVersion, HttpResponseStatus.NOT_FOUND)
        }
      }
  }

  val server = Http.serve(":" + port, service)

  Await.ready(server)

}

