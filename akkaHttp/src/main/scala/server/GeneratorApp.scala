package server

import scala.util.Random
import scalaj.http.Http

/**
  * Created by maria.forero on 3/20/17.
  */
object GeneratorApp extends App {

  val random = Random

  (1 to 1000).map(_ => random.nextInt(999)).map { i => s"maria$i" }.par.map { name =>
    Http("http://localhost:8080/tweets/")
      .postData(s"""{ "user": "$name" , "body" : "this is a tweet"}""")
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .asString
  }.foreach(println)

}
