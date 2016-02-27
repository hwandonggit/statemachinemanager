package com.datainc.pipeline.workflow

import akka.http.scaladsl.marshallers.sprayjson._
import spray.json._

trait TodoMarshalling extends SprayJsonSupport
    with FlowMaterializerProvider
    with DefaultJsonProtocol {

  val standardTodoFormat = jsonFormat4(Todo.apply)

  def todoFormatFor(baseUrl: String) = new RootJsonFormat[Todo] {
    def read(json: JsValue) = standardTodoFormat.read(json)
    def write(todo: Todo) = {
      val fields = standardTodoFormat.write(todo).asJsObject.fields
      JsObject(fields.updated("url", JsString(baseUrl + '/' + todo.txsId)))
    }
  }

  implicit val todoUpdateFormat = jsonFormat3(TodoUpdate.apply)
}
