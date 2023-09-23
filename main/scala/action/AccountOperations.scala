package org.scala
package action

import org.scala.db.PgExecutor
import org.scala.model.Account

object AccountOperations {

  private object AccountSQL {
    val getAccount = "select * from account where account_no = ?"
    val createAccount = "insert into account (account_no,customer_id,balance) values(?,?,?)"
    val creditAmount = "update account set balance= balance + ? where account_no=? returning balance;"
    val debitAmount = "update account set balance= balance - ? where balance > ? and account_no = ? returning balance;"
    val getAllAccount = "select * from account"
  }

  def createAccount(customer_id: Integer): String = {
    val account = Account(customer_id = customer_id)
    val result = PgExecutor.executeSQL(AccountSQL.createAccount, List(account.account_no, account.customer_id, account.getBalance))
    account.account_no
  }

  def getAllAccount:List[Account] = {
    val result = PgExecutor.executeSQL(AccountSQL.getAllAccount,List())
    Account.getAccountList(result)
  }

  def getAccount(account_no: String): Account = {
    val result = PgExecutor.executeSQL(AccountSQL.getAccount, List(account_no))
    Account(result)
  }

  def creditAmount(account: Account): Unit = {
    creditAmount(account.account_no,account.getBalance)
  }
  def creditAmount(account_no: String, amount: Int): Integer = {
    val account = getAccount(account_no)
    //account.credit(amount)
    val result = PgExecutor.executeSQL(AccountSQL.creditAmount, List(amount, account.account_no))
    result.getReturnIntegerValue("balance")
  }

  def debitAmount(account_no:String, amount: Integer):Integer = {
    val account = getAccount(account_no)
    val result = PgExecutor.executeSQL(AccountSQL.debitAmount, List(amount,amount, account.account_no))
    val currentBalance = result.getReturnIntegerValue("balance")
    println("curre "+currentBalance)
    currentBalance
  }


}
