package org.scala.bank
package servlet

import io.circe._
import io.circe.syntax._

import scala.util.{Failure, Success, Try}
import org.scala.bank.upi.{UPIManager, UPITransaction}

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class UPIServlet extends HttpServlet{

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    {
      val account_no = req.getParameter("account_no")
      val upi_id = req.getParameter("upi_id")
      val pin = req.getParameter("pin").toInt

      val status = UPIManager.register(upi_id,account_no,pin)

      resp.setStatus(HttpServletResponse.SC_OK)
      resp.setContentType("text/json")
      val out = resp.getWriter
      out.write(s"{\"status\":\" $status\"}")

      out.close()
    }

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val sender = req.getParameter("sender")
    val receiver = req.getParameter("receiver")
    val amount = req.getParameter("amount").toInt
    val pin = req.getParameter("pin").toInt

    val out = resp.getWriter

    val result = Try {
      val transaction = UPITransaction(sender, receiver, amount)
      val response = UPIManager.send(transaction, pin)
      (HttpServletResponse.SC_OK, Map("message" -> response.remarks))
    } match {
      case Success((status, message)) => (status, message)
      case Failure(e) => (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map("message" -> e.getMessage))
    }

    resp.setStatus(result._1)
    out.write(result._2.asJson.toString)
    out.close()
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val upi = req.getParameter("upi")
    val result = UPIManager.transactionHistory(upi)

    resp.setStatus(HttpServletResponse.SC_OK)
    val out = resp.getWriter
    out.write(result.asJson.toString)
    out.close()

  }

}
