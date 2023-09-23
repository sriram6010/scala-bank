package org.scala
package model

import org.scala.db.ResultSetWrapper
import org.scala.model.{Account, Customer}

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._


import java.util.UUID

//to create a customer Object to DB
case class Customer(customer_id: Integer = 0, name: String, email: String, address: String) {

  private var accounts = List[Account]()

  def getAccount: List[Account] = accounts
  
  private def addAccountDetails(resultSet: ResultSetWrapper): Unit = {
    accounts = Account.getAccountList(resultSet)
  }
}

object Customer {
  //DB to Object
  //only one cx 
  def apply(resultSet: ResultSetWrapper): Customer = {
    val row: List[Map[String, Object]] = resultSet.getResult.head

    val customer_id = row(0)("customer_id").asInstanceOf[Integer]
    val name = row(1)("name").toString
    val email = row(2)("email").toString
    val address = row(3)("address").toString
    val customer = Customer(customer_id, name, email, address)
    customer.addAccountDetails(resultSet)
    customer
  }


  implicit val customerEncoder: Encoder[Customer] = new Encoder[Customer] {
    final def apply(customer: Customer): Json = Json.obj(
      "name" -> Json.fromString(customer.name),
      "customer_id" -> Json.fromInt(customer.customer_id),
      "email" -> Json.fromString(customer.email),
      "address" -> Json.fromString(customer.address),
      "accounts" -> Json.fromValues(customer.accounts.map(account => account.asJson))
    )
  }

  }
