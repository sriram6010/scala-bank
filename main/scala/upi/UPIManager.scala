package org.scala.bank
package upi

import org.postgresql.jdbc.PgResultSet
import org.scala.db.PgExecutor

object UPIManager{
  def send(transaction: UPITransaction,pin: Integer):UPITransaction = {
    val sender = transaction.from
    val receiver = transaction.to

    val debit_transaction = HDFCUPI.debit(transaction, pin)

    val cred_trans = UPITransaction(transaction.from, transaction.to, transaction.amount)

    cred_trans.changeToCredit()
    cred_trans.reference_id = transaction.getTransactionID

    HDFCUPI.credit(cred_trans)
    transaction.reference_id = cred_trans.getTransactionID
    transaction.status = cred_trans.status
    transaction.remarks = cred_trans.remarks
    transaction.handleTransactionStatus()

    transaction
  }

  def register(upiID:String, account_no:String, pin:Int) : String = {
    registerStatus(upiID,account_no,pin)
  }

  def getUPI(account_no:String) : Option[String] ={
    val resultSet = PgExecutor.executeSQL(RegisterSQL.getUPI,List(account_no))
    val upi = resultSet.getReturnStringValueOption("upi_id")
    upi
  }

  def transactionHistory(upiID:String) : List[UPITransaction] = {
    val result = PgExecutor.executeSQL(RegisterSQL.history,List(upiID,upiID))
    val list = UPITransaction.apply(result)
    println(list)
    list
  }


  private val registerStatus: (String, String, Integer) => String = (upiID: String, account_no: String, pin: Integer) => {
    val result = PgExecutor.executeSQL(RegisterSQL.exists, List(upiID))
    val dbUPI = result.getReturnStringValue("upi_id")
    if dbUPI.nonEmpty then s"$upiID already exists"
    else {
      val resultSet = PgExecutor.executeSQL(RegisterSQL.register, List(upiID, pin, account_no))
      if resultSet.getReturnStringValue("upi_id").nonEmpty
      then "Successfully registered"
      else "Failed to register"
    }
  }
}

private object RegisterSQL{
  val register = "insert into upidetail(upi_id,pin,account_no) values (?,?,?) returning upi_id;"
  val exists = "select upi_id from upidetail where upi_id = ?;"
  val getUPI = "select upi_id from upidetail where account_no = ?;"
  val history = "select * from transactionhistory where \"from\" = ? or \"to\" = ?;"
}
