package com.datainc.pipeline.workflow

import scala.concurrent.duration._
import akka.pattern._
import akka.util.Timeout
import akka.cluster.singleton.{ ClusterSingletonProxySettings, ClusterSingletonProxy }
import akka.actor._
import akka.http.scaladsl.Http
import akka.stream._

object PLGateway {
  case object Ok
  case object NotOk
}

class PLGateway extends Actor {
  import PLInterface._
  import context.dispatcher
  val masterProxy = context.actorOf(
    ClusterSingletonProxy.props(
      settings = ClusterSingletonProxySettings(context.system).withRole("backend"),
      singletonManagerPath = "/user/master"),
    name = "masterProxy")

  def receive = {
    case work =>
      implicit val timeout = Timeout(5.seconds)
      (masterProxy ? work) map {
        case Master.Ack(_) => Ok
      } recover { case _ => NotOk } pipeTo sender()

  }

  val port = Properties.envOrElse("PORT", "8080").toInt
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  Http(system).bindAndHandle(routes, "0.0.0.0", port = port)
    .foreach(binding => system.log.info("Bound to " + binding.localAddress))
}