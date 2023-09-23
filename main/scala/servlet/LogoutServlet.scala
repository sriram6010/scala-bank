package org.scala.bank
package servlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

class LogoutServlet extends HttpServlet{

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
      val session = req.getSession
      session.invalidate()
    
    resp.setStatus(HttpServletResponse.SC_OK)
  }
}
