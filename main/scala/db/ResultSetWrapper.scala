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
  
  def getReturnIntegerValue(column_name:String) : Integer = {
    getResult.headOption.flatMap { row =>
      row.headOption.flatMap(_.get(column_name).collect{case value:Int => value})
    }.getOrElse(-1)
  }

  def getReturnStringValue(column_name: String): String = {
    getResult.headOption.flatMap { row =>
      row.headOption.flatMap(_.get(column_name).collect { case value: String => value })
    }.getOrElse("")
  }

  def getReturnStringValueOption(column_name: String): Option[String] = {
    getResult.headOption.flatMap { row =>
      row.headOption.flatMap {
        case map: Map[String, Any] =>
          map.get(column_name) collect {
            case value: String => value
          }
        case null => None
      }
    }
  }

  def extractStringValue(row: List[Map[String, Object]], key: String): String = {
    row.find(map => map.contains(key) && map(key) != null) match {
      case Some(m) => m(key).toString
      case None => ""
    }
  }

  def extractIntValue(row: List[Map[String, Object]], key: String): Int = {
    row.find(map => map.contains(key)) match
      case Some(m) => m(key).asInstanceOf[Int]
      case None => 0
  }

}
