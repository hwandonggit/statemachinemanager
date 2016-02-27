package com.datainc.pipeline.workflow

/**
  * Created by hdong on 2/26/2016.
  */
case class TodoUpdate(state: Option[Int], completed: Option[Boolean], path: Option[String])