package com.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Stuff extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en,es;q=0.8,en-US;q=0.6,de;q=0.4,pt;q=0.2,cs;q=0.2,ru;q=0.2,es-419;q=0.2,en-GB;q=0.2,sq;q=0.2")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36")

  val headers_0 = Map(
    "content-type" -> "application/json")

  val scn = scenario("tweets")
    .exec(http("getAllTweets")
      .get("/tweets/")
      .check(status.is(200))
      .headers(headers_0))

  setUp(scn.inject(constantUsersPerSec(2000).during(10 seconds)))
    .protocols(httpProtocol)
    .assertions(global.responseTime.max.lessThan(200),global.successfulRequests.percent.greaterThan(95))
}