package org.scala.bank
package upi

import org.scala.action.AccountOperations

protected object HDFCUPI extends UPI{

  override def debit(transaction: UPITransaction, pin: Integer): UPITransaction = {
    transaction.handleTransactionStatus()
    verifySuffix(transaction)
    val sender_account_no = getSenderAcc(transaction)
    val rec_acc = getReceiverAcc(transaction)
    verifyPIN(transaction, pin)

    val sen_bal = AccountOperations.debitAmount(sender_account_no,transaction.amount)
    if sen_bal == -1 then {
      transaction.status = TransactionStatus.FAILED
      transaction.remarks = TransactionRemarks.INSUFFICIENT_BALANCE
    } else transaction.remarks = TransactionRemarks.DEBITED

    transaction.handleTransactionStatus()
    transaction
  }

  override def credit(transaction: UPITransaction): UPITransaction = {
    transaction.handleTransactionStatus()

    val rec_acc = getReceiverAcc(transaction)
    val currentBalance = AccountOperations.creditAmount(rec_acc, transaction.amount)
    if currentBalance != -1 then {
      transaction.status = TransactionStatus.COMPLETED
      transaction.remarks = TransactionRemarks.CREDITED
    }else {
      transaction.status = TransactionStatus.FAILED
      transaction.remarks = TransactionRemarks.NOT_CREDITED
    }

    transaction.handleTransactionStatus()
    transaction
  }

  override def verifySuffix(transaction: UPITransaction): UPITransaction = {
    if transaction.from.endsWith("@hdfc") then transaction.remarks = TransactionRemarks.SUFFIX_VERIFIED
    else {
      transaction.remarks = TransactionRemarks.WRONG_FROM
      transaction.status = TransactionStatus.FAILED
    }
    transaction.handleTransactionStatus()
    transaction
  }

}