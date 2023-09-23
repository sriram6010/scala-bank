package org.scala.model

import io.circe.{Encoder, Json}
import io.circe.generic.semiauto.deriveEncoder
import org.scala.bank.upi.UPIManager
import org.scala.db
import org.scala.db.ResultSetWrapper

import java.util.UUID

case class Account(account_no: String = UUID.randomUUID().toString,
                   customer_id: Integer) {
  private var balance: Int = 0
  var upiID = None: Option[String]

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
      val account_no = resultSetWrapper.extractStringValue(row,"account_no")
      val upi = UPIManager.getUPI(account_no)
      val customer_id = resultSetWrapper.extractIntValue(row,"customer_id")
      val balance = resultSetWrapper.extractIntValue(row,"balance")
      val account = Account(account_no = account_no, customer_id = customer_id)
      account.upiID = upi
      account.credit(balance)
      account
    }

  }
  implicit val accountEncoder: Encoder[Account] = new Encoder[Account] {
    final def apply(account: Account): Json = Json.obj(
      "account_no" -> Json.fromString(account.account_no),
      "customer_id" -> Json.fromInt(account.customer_id),
      "balance" -> Json.fromInt(account.getBalance),
      "upi_id" -> account.upiID.fold(Json.Null)(upiID => Json.fromString(upiID))
    )
  }


  def apply(resultSetWrapper: ResultSetWrapper): Account = {
    getAccountList(resultSetWrapper).head
  }




}





