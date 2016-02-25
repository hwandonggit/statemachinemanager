package com.datainc.pipeline.workflow

import java.sql.DriverManager
//import freeslick.OracleProfile.api

trait DatabaseConfig {
  val driver = slick.driver.MySQLDriver
  //val driverOracle = freeslick.driver.oracle.OracleDriver

  import driver.api._

  def db = Database.forConfig("mysqldb")
  //def dbOracle = Database.forConfig("fbio")
  //val url = "jdbc:oracle:thin:@//maui:1521/xe"
  //val dbOracle = Database.forURL(url, driver = "net.sourceforge.jtds.jdbc.Driver")

  implicit val session: Session = db.createSession()
  //implicit val sessionOracle: Session = dbOracle.createSession()
}
