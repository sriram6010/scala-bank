package org.scala.bank
package servlet

import org.scala.action.AccountOperations
import io.circe._
import io.circe.Encoder._
import io.circe.syntax._


import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class AccountServlet extends HttpServlet{

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val customer_id = req.getParameter("customer_id").toInt
    val account_no = AccountOperations.createAccount(customer_id)

    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(s"{\"account_no\":\" ${account_no}\"}")

    out.close()
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val account_no = req.getParameter("account_no")
    val account = AccountOperations.getAccount(account_no)

    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(account.asJson.noSpaces)

    out.close()
  }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val account_no = req.getParameter("account_no")
    val creditAmount = req.getParameter("amount").toInt
    val balance:Integer = AccountOperations.creditAmount(account_no,creditAmount)
    val responseMap = Map(
      "account_no" -> account_no,
      "balance" -> balance.toString
    )

    resp.setContentType("text/json")
    var out = resp.getWriter
    if (balance != -1)
    then {
      resp.setStatus(HttpServletResponse.SC_OK)
      out.write(responseMap.asJson.toString)
    } else
      {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
      }

    out.close()
  }

}
