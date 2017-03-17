package service.domain

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
  * Created by ardilla on 3/16/17.
  */
case class Tweet(user: String, body: String)

object TweetProtocol extends DefaultJsonProtocol{
  implicit val format: RootJsonFormat[Tweet] = jsonFormat2(Tweet.apply)
}


