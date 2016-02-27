package com.datainc.pipeline.workflow

/**
  * Created by hdong on 2/26/2016.
  */
import akka.actor._

trait TodoStorageProvider {
  val todoStorage: ActorRef
}
