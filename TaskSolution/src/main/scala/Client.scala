import scala.collection.mutable
import Shares._

case class Client(name: String, var balance: Long, var shares: mutable.HashMap[String, Long]) {
  def changeBalance(change: Long): Unit = balance += change
  def changeShares(share: String, count: Long): Unit = shares(share) += count

  override def toString: String = {
    s"$name\t$balance\t${shares(A)}\t${shares(B)}\t${shares(C)}\t${shares(D)}"
  }
}
