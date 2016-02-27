package com.datainc.pipeline.workflow

/**
  * Created by hdong on 2/26/2016.
  */
import scala.util.Random

case class Todo(id: String, title: String, completed: Boolean = false, order: Int = 0)
case object Todo {
  private def nextId() =  Random.nextInt(Integer.MAX_VALUE).toString

  def create(title: String, todoUpdate: TodoUpdate): Todo = {
    Todo.create(Todo(nextId(), title), todoUpdate)
  }

  def create(old: Todo, update: TodoUpdate): Todo =
    Todo(old.id,
      update.title.getOrElse(old.title),
      update.completed.getOrElse(old.completed),
      update.order.getOrElse(old.order))
}

