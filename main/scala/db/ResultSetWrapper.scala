package org.scala
package db

import java.sql.ResultSet
import scala.collection.mutable.ListBuffer

class ResultSetWrapper {
  private var result: List[List[Map[String, Object]]] = List[List[Map[String,Object]]]()
  private var columnNames: List[String] = List[String]()

  def wrap(resultSet: ResultSet):Unit = {
    val metaData = resultSet.getMetaData
    val columnCount = metaData.getColumnCount
    columnNames = (1 to columnCount).map(i => metaData.getColumnName(i)).toList
    val resultBuffer: ListBuffer[List[Map[String, Object]]] = ListBuffer[List[Map[String, Object]]]()

    while (resultSet.next()) {
      val rowBuffer: ListBuffer[Map[String, Object]] = ListBuffer[Map[String, Object]]()
      columnNames.foreach { column =>
        rowBuffer += Map(column -> resultSet.getObject(column))
      }
      resultBuffer += rowBuffer.toList
    }
    result = resultBuffer.toList
  }

  def getResult: List[List[Map[String, Object]]] = result
}
