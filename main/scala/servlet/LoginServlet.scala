package org.scala.bank
package servlet

import org.scala.action.CustomerOperations
import org.scala.bank.auth.AuthUtil

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class LoginServlet extends HttpServlet{

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val customer_id = req.getParameter("customer_id").toInt
    val password = req.getParameter("password")
    val hashedPassword = AuthUtil.hashPassword(password)
    
    val isValidUser = CustomerOperations.login(customer_id,hashedPassword)
    if isValidUser then {
      val session = req.getSession(true)
      session.setAttribute("session_cus_id",customer_id)
    } else {
      resp.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Wrong customer_id/password")
    }
    
  }

}
