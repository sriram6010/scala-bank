package org.scala.bank
package servlet

import org.scala.action.CustomerOperations

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class SignUpServlet extends HttpServlet{

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val name = req.getParameter("name")
    val email = req.getParameter("email")
    val address = req.getParameter("address")
    val password = req.getParameter("password")

    val customer_id = CustomerOperations.createCustomer(name, email, address, password)

    val session = req.getSession(true)
    session.setAttribute("session_cus_id", customer_id)


    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(s"{\"customer_id\":\" ${customer_id}\"}")

    out.close()
  }
}
