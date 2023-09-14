package org.scala.model

import io.circe.{Encoder, Json}
import io.circe.generic.semiauto.deriveEncoder
import org.scala.db.ResultSetWrapper

import java.util.UUID

case class Account(account_no: String = UUID.randomUUID().toString,
                   customer_id: String) {
  private var balance: Int = 0

  def credit(amount: Int): Unit = {
    balance += amount
  }

  def getBalance: Int = balance
}


private object AccountColumns {
  val account_no = "account_no"
  val customer_id = "customer_id"
  val balance = "balance"
}


object Account {

  def getAccountList(resultSetWrapper: ResultSetWrapper): List[Account] = {
    val result = resultSetWrapper.getResult
    result.map { row =>
      val account_no = extractStringValue(row,"account_no")

      val customer_id = extractStringValue(row,"customer_id")
      val balance = extractIntValue(row,"balance")
      val account = Account(account_no = account_no, customer_id = customer_id)
      account.credit(balance)
      account
    }

  }
  implicit val accountEncoder: Encoder[Account] = new Encoder[Account] {
    final def apply(account: Account): Json = Json.obj(
      "account_no" -> Json.fromString(account.account_no),
      "customer_id" -> Json.fromString(account.customer_id),
      "balance" -> Json.fromInt(account.getBalance)
    )
  }


  def apply(resultSetWrapper: ResultSetWrapper): Account = {
    getAccountList(resultSetWrapper).head
  }

  def extractStringValue(row: List[Map[String, Object]], key: String): String = {
    row.find(map => map.contains(key) && map(key)!=null) match {
      case Some(m) => m(key).toString
      case None => ""}
  }

  def extractIntValue(row: List[Map[String, Object]], key: String): Int = {
    row.find(map => map.contains(key)) match
      case Some(m) => m(key).asInstanceOf[Int]
      case None => 0
  }


}





