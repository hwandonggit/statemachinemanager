package com.datainc.pipeline.workflow

trait TxsTable extends DatabaseConfig {

  import driver.api._

  class Todos(tag: Tag) extends Table[Todo](tag, "todos") {
    def txsId = column[String]("txsId", O.PrimaryKey)
    def state = column[Int]("state")
    def completed = column[Boolean]("completed")
    def path = column[String]("path")

    def * = (txsId, state, completed, path) <> ((Todo.apply _).tupled, Todo.unapply)
  }

  protected val todos = TableQuery[Todos]
}
