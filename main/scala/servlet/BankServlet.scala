package org.scala.bank
package servlet

import org.scala.action.{AccountOperations, CustomerOperations}

import java.io.{BufferedReader, InputStreamReader, Reader}
import java.nio.charset.Charset
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class BankServlet extends HttpServlet{

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val balance = AccountOperations.getAccount("a092a79b-63f3-4b33-aec8-44d0393e085d").getBalance
    resp.setContentType("text/html;charset=UTF-8")
    val out = resp.getWriter
    out.println(balance)
    out.close()
  }
}
