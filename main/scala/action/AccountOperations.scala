package org.scala
package action

import org.scala.db.PgExecutor
import org.scala.model.Account

object AccountOperations {

  private object AccountSQL {
    val getAccount = "select * from account where account_no = ?"
    val createAccount = "insert into account (account_no,customer_id,balance) values(?,?,?)"
    val creditAmount = "update account set balance=? where account_no=?"
    val getAllAccount = "select * from account"
  }

  def createAccount(customer_id: String): String = {
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
  def creditAmount(account_no: String, amount: Int): Unit = {
    val account = getAccount(account_no)
    account.credit(amount)
    val result = PgExecutor.executeSQL(AccountSQL.creditAmount, List(account.getBalance, account.account_no))
    println(result.toString)
  }


}
