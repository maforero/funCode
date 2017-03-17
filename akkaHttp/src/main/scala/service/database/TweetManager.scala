package service.database


import scala.concurrent.ExecutionContext
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection
import service.entities._

/**
  * Created by maria.forero on 17/03/2017.
  */
object TweetManager {
  import MongoDB._
  import TweetEntity._

  val collection = db[BSONCollection]("tweets")

  def save(tweetEntity: TweetEntity)(implicit ec: ExecutionContext) =
    collection.insert(tweetEntity).map(_ => Created(tweetEntity.id.stringify))

  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[TweetEntity]

  def deleteById(id: String)(implicit ec: ExecutionContext) =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find(implicit ec: ExecutionContext) =
    collection.find(emptyQuery).cursor[BSONDocument].collect[List]()

  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def emptyQuery = BSONDocument()
}