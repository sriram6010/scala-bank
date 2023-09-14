package org.scala
package db

import org.postgresql.core.SqlCommand

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

object PgExecutor {
  val url = "jdbc:postgresql://localhost:5433/bank"
  val userName = "postgres"
  val password = "postgres"

  private def getConnection: Connection = {
    Class.forName("org.postgresql.Driver")
    DriverManager.getConnection(url, userName, password)
  }

  def executeSQL(sqlString: String,args:List[Any]): ResultSetWrapper = {
    val resultSetWrapper: ResultSetWrapper = new ResultSetWrapper
    var connection:Connection = null
    var preparedStatement: PreparedStatement = null
    var resultSet:ResultSet = null
    try {

      connection = getConnection
      preparedStatement = connection.prepareStatement(sqlString)
      for (index <- args.indices) {
        preparedStatement.setObject(index+1, args(index))
      }
      resultSet = preparedStatement.executeQuery()
      resultSetWrapper.wrap(resultSet)

    } catch {
      case exception: Exception => println(exception.getMessage)
    } finally {
      if (preparedStatement != null) preparedStatement.close()
      if (connection != null) connection.close()
      if (resultSet != null) connection.close()
    }
    println(resultSetWrapper.getResult)
    resultSetWrapper
  }
}

