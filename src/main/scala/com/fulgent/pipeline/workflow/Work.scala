package com.fulgent.pipeline.workflow

case class Work(workId: String, job: Any)

case class WorkResult(workId: String, result: Any)

case class DataValidation(workId: String, job: Any)

case class Annotation(workId: String, job: Any)