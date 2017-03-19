package service.database

import com.typesafe.config.ConfigFactory
import reactivemongo.api.MongoDriver

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global




/**
  * Created by maria.forero on 17/03/2017.
  */
object MongoDB {
  val config = ConfigFactory.load()
  val database = config.getString("mongodb.database")
  val servers = config.getStringList("mongodb.servers").asScala

  val driver = new MongoDriver
  val connection = driver.connection(servers)

  val db = connection(database)

}
