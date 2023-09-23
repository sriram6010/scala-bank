package org.scala.bank
package upi

import org.scala.db.{PgExecutor, ResultSetWrapper}

protected trait UPI (){

  private object UPISQL {
    val verifyPIN = "select upi_id from upidetail where upi_id = ? and pin = ?;"
    val verifyID = "select account_no from upidetail where upi_id = ?;"
  }
  
  def credit(transaction: UPITransaction): UPITransaction

  def debit(transaction: UPITransaction, pin: Integer): UPITransaction
  
  def verifyPIN(transaction: UPITransaction, pin:Integer): UPITransaction = {
    val sender = transaction.from
    val result = PgExecutor.executeSQL(UPISQL.verifyPIN, List(sender, pin))
    val upi_id = result.getReturnStringValue("upi_id")

    if sender.equals(upi_id) then transaction.remarks = TransactionRemarks.PIN_VERIFIED
    else {
      transaction.remarks = TransactionRemarks.WRONG_PIN
      transaction.status = TransactionStatus.FAILED
    }

    transaction.handleTransactionStatus()
    transaction
  }

  def verifySuffix(transaction: UPITransaction):UPITransaction
  
  def getSenderAcc(transaction: UPITransaction) : String = {
    getAccountNo(transaction,true)
  }
  def getReceiverAcc(transaction: UPITransaction) : String = {
    getAccountNo(transaction,false)
  }

  private def getAccountNo(transaction: UPITransaction, forSender:Boolean): String = {
    val upiID = if forSender then transaction.from else transaction.to
    val result = PgExecutor.executeSQL(UPISQL.verifyID,List(upiID))
    val account_no = result.getReturnStringValue("account_no")
    if account_no.nonEmpty then transaction.remarks = TransactionRemarks.RECEIVER_AVAILABLE
    else {
      transaction.remarks = TransactionRemarks.RECEIVER_UNAVAILABLE
      transaction.status = TransactionStatus.FAILED
    }
    transaction.handleTransactionStatus()
    account_no
  }
}






