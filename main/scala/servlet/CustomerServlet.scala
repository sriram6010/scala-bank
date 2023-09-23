package org.scala.bank
package servlet

import org.scala.action.CustomerOperations
import io.circe.syntax.*
import org.scala.bank.auth.AuthUtil

import java.util.UUID
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class CustomerServlet extends HttpServlet{

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val customer_id = req.getParameter("customer_id").toInt
    val customer = CustomerOperations.getCustomer(customer_id)

    resp.setStatus(HttpServletResponse.SC_OK)
    resp.setContentType("text/json")
    var out = resp.getWriter
    out.write(customer.asJson.noSpaces)

    out.close()
  }
}
