package service.entities

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}
import service.domain.Tweet
import spray.json._
import scala.util._


/**
  * Created by maria.forero on 3/16/17.
  */
case class TweetEntity(id: BSONObjectID = BSONObjectID.generate,
                       user: String,
                       body: String)


  object TweetEntity{

    implicit def toTweetEntity(tweet: Tweet) = TweetEntity(user = tweet.user,body = tweet.body)

    implicit object TweetEntityBSONReader extends BSONDocumentReader[TweetEntity]{
       def read(doc: BSONDocument): TweetEntity =
         TweetEntity(
           id = doc.getAs[BSONObjectID]("_id").get,
           user = doc.getAs[String]("_user").get,
           body = doc.getAs[String]("_body").get
         )
    }

    implicit object TweetEntityBSONWriter extends BSONDocumentWriter[TweetEntity]{
        def write(entity: TweetEntity): BSONDocument =
          BSONDocument(
            "_id" -> entity.id,
            "_user" -> entity.user,
            "_body" -> entity.body
          )
    }
  }

object TweetEntityProtocol extends DefaultJsonProtocol {

    implicit object BSONObjectIdProtocol extends RootJsonFormat[BSONObjectID] {
      override def write(obj: BSONObjectID): JsValue = JsString(obj.stringify)
      override def read(json: JsValue): BSONObjectID = json match {
        case JsString(id) => BSONObjectID.parse(id) match {
          case Success(validId) => validId
          case _ => deserializationError("Invalid BSON Object Id")
        }
        case _ => deserializationError("BSON Object Id expected")
      }
    }

  implicit val EntityFormat = jsonFormat3(TweetEntity.apply)
}


