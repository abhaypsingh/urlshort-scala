package lv.sa1nt.urlshort.scala

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import org.apache.commons.validator.routines.UrlValidator
import spray.http.StatusCodes
import spray.routing.SimpleRoutingApp
import spray.routing.RequestContext
import spray.routing.ValidationRejection
import scala.util.Random
import scalaz.concurrent.Future
import scalaz.concurrent.Task

object UrlShortenerController extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("turbo-url-shortener")
//  implicit val redis = scredis.Redis("application.conf", "development.scredis");
//  implicit val redis = scredis.Redis("application.conf");
//  val config: Config = ConfigFactory.load("redis.conf");
//  println(config);
//  println(config.getConfig("scredis"));

  implicit val redis = scredis.Redis(ConfigFactory.load("redis.conf"))

  startServer("0.0.0.0", 8080) {
    path(Rest) { r =>
      get {
        redirectShortUrl(r)
      } ~ post {
        createShortUrl(r)
      }
    }
  }

  def redirectShortUrl(path: String)(implicit redis: scredis.Redis) = (ctx: RequestContext) => {
    Future.now {
      redis.withClient(_.get[String](path))
    }.runAsync(
      _.map(ctx.redirect(_, StatusCodes.MovedPermanently)).getOrElse(ctx.complete(StatusCodes.NotFound))
    )
  }

  def createShortUrl(path: String)(implicit redis: scredis.Redis) = (ctx: RequestContext) => {
    Task {
      val validator = UrlValidator.getInstance
      if (validator.isValid(path)) {
        val rand = Random.alphanumeric.take(7).mkString
        redis.withClient(_.set(rand, path))
        rand
      } else {
        throw new Exception("Invalid url")
      }
    }.runAsync(
      _.fold(
        l => ctx.reject(ValidationRejection("Invalid url", Some(l))),
        r => ctx.complete(s"http://myshortdomain.com/$r")
      )
    )
  }
}
