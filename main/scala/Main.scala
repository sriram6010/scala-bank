package org.scala

import org.scala.action.{AccountOperations, CustomerOperations}
import org.scala.model.Customer
import io.circe.syntax.*
import org.scala.bank.upi.{HDFCUPI, TransactionFailureException, TransactionRemarks, TransactionStatus, TransactionType, UPIManager, UPITransaction}
import io.circe._

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    val history = UPIManager.transactionHistory("sriram@hdfc");

    println(history.asJson)
    //println(CustomerOperations.getCustomer(22).asJson.noSpaces)
   /*val transaction = UPITransaction("sriram@hdfc","ram@hdfc",2)
    val tran = UPIManager.send(transaction,1234)
    println(tran.asJson)*/

   //// val manager = UPIManager.register("accnt@hdfc","accc",1234)

    //println(s"main $manager")
   // val acc = AccountOperations.createAccount(19)

   // println(AccountOperations.debitAmount("aaac6412-873e-499c-9ae6-5654574e1aaa",123))


  /* println("new "+CustomerOperations.createCustomer("Sriram","sri@gmail.com","Tenkasi","mypassword").asJson.noSpaces)
    println(AccountOperations.createAccount("0cd90223-05a7-4262-b237-45780c1a4957"))
   val customer = CustomerOperations.getCustomer(10)
   // val string = customer.asJson.noSpaces
   println("main "+customer)
    //val account = customer.getAccount.head
    //account.credit(100)
    //AccountOperations.creditAmount(account)
    //println(AccountOperations.getAccount("a092a79b-63f3-4b33-aec8-44d0393e085d").getBalance)*/

  }


}
