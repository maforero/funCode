package service.entities

import reactivemongo.bson.BSONObjectID

/**
  * Created by ardilla on 3/16/17.
  */
case class TweetEntity(id: BSONObjectID = BSONObjectID.generate,
                       user: String,
                       body: String) {

}
