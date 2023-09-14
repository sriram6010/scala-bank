package org.scala.bank
package servlet

import org.scala.action.CustomerOperations

import io.circe.syntax._


import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class CustomerServlet extends HttpServlet{

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val name = req.getParameter("name")
    val email = req.getParameter("email")
    val address = req.getParameter("address")
    val customer_id = CustomerOperations.createCustomer(name, email, address)

    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(s"{\"customer_id\":\" ${customer_id}\"}")

    out.close()
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val customer_id = req.getParameter("customer_id")
    val customer = CustomerOperations.getCustomer(customer_id)

    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(customer.asJson.noSpaces)

    out.close()
  }
}
