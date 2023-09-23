package org.scala.bank
package auth

import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import javax.xml.bind.DatatypeConverter

object AuthUtil {
  def main(args: Array[String]): Unit = {
    // Define a password to be hashed
    val plainPassword = "sriram"

    // Hash the password using SHA-256
    val hashedPassword = hashPassword(plainPassword)

    // Print the hashed password
    println(s"Original Password: $plainPassword")
    println(s"Hashed Password: $hashedPassword")

    // Simulate login by verifying a user-provided password
    val userInputPassword = "sriram" // Replace with user input
    if (verifyPassword(userInputPassword, hashedPassword)) {
      println("Password is valid")
    } else {
      println("Password is invalid")
    }
  }

  def hashPassword(password: String): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8))
    DatatypeConverter.printHexBinary(hashedBytes).toLowerCase
  }

  def verifyPassword(userInputPassword: String, hashedPassword: String): Boolean = {
    val hashedInputPassword = hashPassword(userInputPassword)
    hashedInputPassword == hashedPassword
  }
}

