import org.scalatest._
import Main._
import Shares._
class Test extends FlatSpec with Matchers {
  "A matching" should "math sell orders and buy orders with the same shares' prices and amounts" in {
    readData("clients1.txt", "orders1.txt")
    matching
    clients(0).balance should be (1060)
    clients(0).shares(A) should be (126)

    clients(1).balance should be (4350)

    clients(2).balance should be (2700)
    clients(2).shares(A) should be (4)

    clients(3).balance should be (560)
  }
}
