package service.database


import scala.concurrent.{ExecutionContext, Future}
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

  def findByUser(user: String)(implicit ec: ExecutionContext) = {
    val f = collection.find(queryByUser(user)).one[TweetEntity]
    f.onFailure {
      case e: Exception => e.printStackTrace()
    }
    f.onComplete(l => println(l))
    f
  }


  def deleteByUser(user: String)(implicit ec: ExecutionContext) =  {
    val f = collection.remove(queryByUser(user)).map(_ => Deleted)
    f.onFailure{
      case e: Exception => e.printStackTrace()
    }
    f
  }


  def find(implicit ec: ExecutionContext) : Future[List[TweetEntity]] = {
    val f = collection.find(emptyQuery).cursor().collect[List]()
    f.onFailure{
      case e: Exception => e.printStackTrace()
    }
    f.onComplete(l => print(l))
    f
  }


  private def queryByUser(user: String) = BSONDocument("_user" -> user)

  private def emptyQuery = BSONDocument()
}