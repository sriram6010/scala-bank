package org.scala.bank
package filter

import java.util
import java.util.Arrays
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{Filter, FilterChain, FilterConfig, ServletRequest, ServletResponse}

class AuthenticationFilter extends Filter{

  val exclude:List[String] = List("/bank-app/login","/bank-app/signup","/bank-app/logout")

  override def init(filterConfig: FilterConfig): Unit = {
    val excludeString = filterConfig.getInitParameter("exclude")
    //exclude = excludeString.split(",").toList

  }

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    val httpRequest = request.asInstanceOf[HttpServletRequest]
    val httpResponse = response.asInstanceOf[HttpServletResponse]

    val requestURI = httpRequest.getRequestURI
    println("list "+exclude.size)
    println("url "+requestURI)
    println("requri ? "+exclude.exists(url => url.toString.equalsIgnoreCase(requestURI.toString)))
    println(" session "+httpRequest.getSession.getAttribute("session_cus_id"))

    if (!exclude.exists(url => url.equalsIgnoreCase(requestURI)) && httpRequest.getSession.getAttribute("session_cus_id") == null) {
      println("inside if")
      /*val redirect = httpRequest.getHeader("Referer")//+"#/login"
      println(redirect)*/
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
      //httpResponse.sendRedirect(redirect)
    } else {
      chain.doFilter(request, response)
    }
  }
}
