package com.fulgent.pipeline.workflow

class Annotator extends Worker() {
  override def working: Receive = {
    case Annotation(result) =>
      // call bam2variants with the following parameters: 
      // bam2variants accesionID panelbase genelist testID customerTestName gender
      log.info("TBD.")

    case WorkComplete(result) =>
      log.info("Work is complete. Result {}.", result)
      sendToMaster(WorkIsDone(workerId, workId, result))
      context.setReceiveTimeout(5.seconds)
      context.become(waitForWorkIsDoneAck(result))

    case _: Work =>
      log.info("Yikes. Master told me to do work, while I'm working.")
  }
}