package com.datainc.pipeline.workflow

/**
  * Created by hdong on 2/26/2016.
  */
import scala.util.Random

case class Todo(txsId: String, state: Int, completed: Boolean, path: String)
case object Todo {
  private def nextId() =  Random.nextInt(Integer.MAX_VALUE).toString

  def create(state: Int, todoUpdate: TodoUpdate): Todo = {
    Todo.create(Todo(nextId(), state, false, ""), todoUpdate)
  }

  def create(old: Todo, update: TodoUpdate): Todo =
    Todo(old.txsId,
      update.state.getOrElse(old.state),
      update.completed.getOrElse(old.completed),
      update.path.getOrElse(old.path))
}

