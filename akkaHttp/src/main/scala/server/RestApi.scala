package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import service.database._
import service.domain._
import service.entities._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

trait RestApi {
  import TweetEntity._
  import TweetEntityProtocol.EntityFormat
  import TweetProtocol._

  implicit val system: ActorSystem

  implicit val materializer: Materializer

  implicit val ec: ExecutionContext

  val route: Route ={
    pathPrefix("tweets"){
      post {
        entity(as[Tweet]){
          tweet =>
            complete{
              TweetManager.save(tweet) map{ r =>
                StatusCodes.Created -> Map("id" -> r.id).toJson
              }
            }
        }
      } ~
      get {
        path(Segment){
          id =>
            complete {
              TweetManager.findByUser(id) map { t =>
                StatusCodes.OK -> t
              }
            }
        }
      } ~
      delete {
        path(Segment){
          id =>
            complete{
              TweetManager.deleteByUser(id) map { _ =>
                StatusCodes.NoContent
              }
            }
        }
      } ~
      get{
        complete{
          TweetManager.find map { ts =>
            StatusCodes.OK -> ts
          }
        }
      }
    }
  }


}
object Api extends App with RestApi {

  override implicit val system = ActorSystem("rest-api")

  override implicit val materializer = ActorMaterializer()

  override implicit val ec = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())

}