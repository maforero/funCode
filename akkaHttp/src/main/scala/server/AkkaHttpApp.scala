package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Sink


/**
  * Created by maria.forero on 16/03/2017.
  */
object AkkaHttpApp extends App{

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val serverSource: Source[Http.IncomingConnection,  Future[Http.ServerBinding]] = Http().bind(interface = "localhost",  port = 8080)

  val route : Route = {
    path("health") {
      get {
        complete(StatusCodes.OK,"Everything is great!")
      }
    }
  }

  val sink = Sink.foreach[Http.IncomingConnection](_.handleWith(route))
  serverSource.to(sink).run

}