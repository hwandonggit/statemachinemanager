package com.datainc.pipeline.workflow

import scala.concurrent.duration.DurationInt

import com.typesafe.config.ConfigFactory

import akka.actor.ActorIdentity
import akka.actor.ActorPath
import akka.actor.ActorSystem
import akka.actor.AddressFromURIString
import akka.actor.Identify
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.RootActorPath
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.japi.Util.immutableSeq
import akka.pattern.ask
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.persistence.journal.leveldb.SharedLeveldbStore
import akka.util.Timeout

//import akka.actor._
import akka.http.scaladsl.Http
import akka.stream._


import scala.util._

object Main extends App
  with TodoManager
  with TodoRoutes
  with TxsTable {
  val port = Properties.envOrElse("PORT", "8080").toInt
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  //  import driver.api._
  //  val setupAction: DBIO[Unit] = DBIO.seq(todos.schema.create)
  //  Await.result(db.run(setupAction), Duration.Inf)

  Http(system).bindAndHandle(routes, "0.0.0.0", port = port)
    .foreach(binding => system.log.info("Bound to " + binding.localAddress))

  startBackend(2551, "backend")
  startBackend(2552, "backend")
  startWorker(0)

  def workTimeout = 10.seconds

  def startBackend(port: Int, role: String): Unit = {
    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
      withFallback(ConfigFactory.load())
    val system = ActorSystem("ClusterSystem", conf)

    startupSharedJournal(system, startStore = (port == 2551), path =
      ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"))

    system.actorOf(
      ClusterSingletonManager.props(
        Manager.props(workTimeout),
        PoisonPill,
        ClusterSingletonManagerSettings(system).withRole(role)),
      "master")

  }

  def startWorker(port: Int): Unit = {
    // load worker.conf
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load("worker"))
    val system = ActorSystem("WorkerSystem", conf)
    val initialContacts = immutableSeq(conf.getStringList("contact-points")).map {
      case AddressFromURIString(addr) ⇒ RootActorPath(addr) / "system" / "receptionist"
    }.toSet

    val clusterClient = system.actorOf(
      ClusterClient.props(
        ClusterClientSettings(system)
          .withInitialContacts(initialContacts)),
      "clusterClient")

    system.actorOf(Worker.props(clusterClient, Props[WorkExecutor]), "worker")
  }

  def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    // Start the shared journal one one node (don't crash this SPOF)
    // This will not be needed with a distributed journal
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")
    // register the shared journal
    import system.dispatcher
    implicit val timeout = Timeout(15.seconds)
    val f = (system.actorSelection(path) ? Identify(None))
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }
}
