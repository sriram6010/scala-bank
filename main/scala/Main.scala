package org.scala

import org.scala.action.{AccountOperations, CustomerOperations}

import org.scala.model.Customer
import io.circe.syntax._


object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")


   // println("new "+CustomerOperations.createCustomer("Sriram","sri@gmail.com","Tenkasi").asJson.noSpaces)
    //println(AccountOperations.createAccount("0cd90223-05a7-4262-b237-45780c1a4957"))
    val customer = CustomerOperations.getCustomer("32a12921-3d87-4bc9-ac15-2aeed0a99332")
    val string = customer.asJson.noSpaces
    println("main "+string)
    //val account = customer.getAccount.head
    //account.credit(100)
    //AccountOperations.creditAmount(account)
    //println(AccountOperations.getAccount("a092a79b-63f3-4b33-aec8-44d0393e085d").getBalance)

  }


}
