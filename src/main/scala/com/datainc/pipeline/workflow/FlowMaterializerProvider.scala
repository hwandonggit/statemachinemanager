package com.datainc.pipeline.workflow
import akka.stream._

trait FlowMaterializerProvider {
  implicit val materializer: Materializer
}
