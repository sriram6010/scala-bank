package org.scala
package action

import org.scala.db.PgExecutor
import org.scala.model.Customer


object CustomerOperations {

  private object CustomerSQL{
    val getOneCustomer = "select customer.customer_id,customer.name,customer.email,customer.address,account.account_no,account.balance from customer left join account on customer.customer_id = account.customer_id where customer.customer_id = ?"
    val createCustomer = "insert into customer(customer_id,name,email,address) values(?,?,?,?)"
  }

  def getCustomer(customer_id: String): Customer = {
    val resultSetWrapper = PgExecutor.executeSQL(CustomerSQL.getOneCustomer,List(customer_id))
    Customer(resultSetWrapper)
  }

  def createCustomer(name: String, email: String, address: String): String = {
    val customer = Customer(name = name,email = email, address = address)
    PgExecutor.executeSQL(CustomerSQL.createCustomer,List(customer.customer_id,customer.name,customer.email,customer.address))
    customer.customer_id
  }

}
