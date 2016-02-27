package com.datainc.pipeline.workflow

/**
  * Created by hdong on 2/26/2016.
  */
case class TodoUpdate(title: Option[String], completed: Option[Boolean], order: Option[Int])