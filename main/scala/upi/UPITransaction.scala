package org.scala.bank
package upi

import io.circe.{Encoder, Json}
import org.scala.db.{PgExecutor, ResultSetWrapper}

case class UPITransaction(from:String, to:String,
                          amount:Integer)
{
  private var transaction_id = -1
  var reference_id = -1
  protected var trans_type = TransactionType.DEBIT
  var status:Integer = TransactionStatus.STARTED
  var remarks:String = TransactionRemarks.NEW

  def getTransactionID: Integer ={
    this.transaction_id
  }
  
  protected[upi] def changeToCredit():Unit = {
    this.trans_type = TransactionType.CREDIT
  }

  protected[upi] def handleTransactionStatus() : Unit = {
    println(s"Current status: ${this.remarks}")
    if (this.remarks == TransactionRemarks.NEW ||
      List(TransactionStatus.FAILED,TransactionStatus.COMPLETED).contains(this.status)) {
      this.persist()
    }
      if this.status == TransactionStatus.FAILED then {
      throw new TransactionFailureException(500, s"Failure: ${this.remarks}")

    }
  }

  private def persist(): UPITransaction = {
    if (this.transaction_id == -1) {
      val result = PgExecutor.executeSQL(historySQL.addTransaction, List(this.from, this.to, this.trans_type, this.amount, this.status, this.remarks))
      val transaction_id = result.getReturnIntegerValue("transaction_id")
      this.transaction_id = transaction_id
    } else {
      val result = PgExecutor.executeSQL(historySQL.updateTransaction, List(this.status, this.remarks, this.reference_id,this.transaction_id))
      val transaction_id = result.getReturnIntegerValue("transaction_id")
    }
    println(s"Persist : ${this.remarks}")
    this
  }
}

object UPITransaction{

  def apply(resultSetWrapper: ResultSetWrapper): List[UPITransaction] = {
    val result = resultSetWrapper.getResult
    result.map {
      row =>
        val transaction_id = resultSetWrapper.extractIntValue(row,"transaction_id")
        val from = resultSetWrapper.extractStringValue(row,"from")
        val to = resultSetWrapper.extractStringValue(row,"to")
        val trans_type = resultSetWrapper.extractIntValue(row,"type")
        val amount = resultSetWrapper.extractIntValue(row,"amount")
        val status = resultSetWrapper.extractIntValue(row,"status")

        val remarks = resultSetWrapper.extractStringValue(row,"remarks")
        val ref_id = resultSetWrapper.extractStringValue(row,"reference_id")

        val transaction = UPITransaction(from,to, amount)
        transaction.transaction_id = transaction_id
        transaction.remarks = remarks
        transaction.reference_id = ref_id.toInt
        transaction.trans_type = trans_type
        transaction.status = status
        transaction
    }
  }

  implicit val accountEncoder: Encoder[UPITransaction] = new Encoder[UPITransaction] {
    final def apply(transation: UPITransaction): Json = Json.obj(
      "transaction_id"-> Json.fromInt(transation.transaction_id),
      "sender" -> Json.fromString(transation.from),
      "receiver" -> Json.fromString(transation.to),
      "amount" -> Json.fromInt(transation.amount),
      "transaction_type" -> Json.fromString(transation.trans_type match {
      case TransactionType.CREDIT => "Credit"
      case TransactionType.DEBIT => "Debit"
    }),
      "transaction_status" -> Json.fromString(transation.status match
        case TransactionStatus.STARTED => "Started"
        case TransactionStatus.COMPLETED => "Completed"
        case TransactionStatus.FAILED => "Failed"),
      "remarks" -> Json.fromString(transation.remarks),
      "reference_id" -> Json.fromInt(transation.reference_id)
    )
  }
}

private object historySQL {
  val addTransaction = "insert into transactionhistory (\"from\",\"to\",type,amount,status,remarks) values (?,?,?,?,?,?) returning transaction_id;"
  val updateTransaction = "update transactionhistory set status=?,remarks=?,reference_id = ? where transaction_id = ? returning transaction_id;"
}

object TransactionType{
  val CREDIT = 0
  val DEBIT = 1
}

object TransactionStatus {
  val STARTED = 0
  val COMPLETED = 1
  val FAILED = 2
}

object TransactionRemarks {
  val WRONG_PIN = "Wrong PIN"
  val WRONG_FROM = "Wrong FROM"
  val RECEIVER_UNAVAILABLE = "UPI unavailable"
  val RECEIVER_AVAILABLE = "UPI available"
  val PIN_VERIFIED = "PIN verified"
  val SUFFIX_VERIFIED = "Suffix verified"
  val NEW = "NEW"
  val INSUFFICIENT_BALANCE = "Insufficient balance"
  val DEBITED = "Debited"
  val CREDITED = "Credited"
  val NOT_CREDITED = "Not credited"
}


class TransactionFailureException(errorCode: Integer, message: String) extends Exception(message)


