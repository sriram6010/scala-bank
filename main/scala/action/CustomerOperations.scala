package org.scala
package action

import org.scala.bank.auth.AuthUtil
import org.scala.db.{PgExecutor, ResultSetWrapper}
import org.scala.model.Customer
import sun.security.util.Password

import java.util.UUID


object CustomerOperations {

  private object CustomerSQL{
    val getOneCustomer = "select customer.customer_id,customer.name,customer.email,customer.address,account.account_no,account.balance from customer left join account on customer.customer_id = account.customer_id where customer.customer_id = ?"
    val createCustomer = "insert into customer(name,email,address) values(?,?,?) returning customer_id;"
    val createCustomerWithPass = "with new_cus as (insert into customer(name, email,address) values (?,?,?) returning customer_id ) insert into secret (customer_id, password) select customer_id,? from new_cus returning customer_id;"
    val login = "select customer_id from secret where customer_id = ? and password = ?;"
  }

  def getCustomer(customer_id: Integer): Customer = {
    val resultSetWrapper = PgExecutor.executeSQL(CustomerSQL.getOneCustomer,List(customer_id))
    Customer(resultSetWrapper)
  }
  
  def login(customer_id: Integer, password: String): Boolean = {
    val resultSetWrapper: ResultSetWrapper = PgExecutor.executeSQL(CustomerSQL.login, List(customer_id, password))
    val result_customer_id = resultSetWrapper.getReturnIntegerValue("customer_id")
    result_customer_id != -1
  }

  def createCustomer(name: String, email: String, address: String,password: String): Integer = {
    val hashedPass = AuthUtil.hashPassword(password)

    val customer = Customer(name = name, email = email, address = address)
    val result = PgExecutor.executeSQL(CustomerSQL.createCustomerWithPass, List(customer.name, customer.email, customer.address,hashedPass))
    result.getReturnIntegerValue("customer_id")
  }

}
