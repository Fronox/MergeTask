trait Order {
  val client: Client
  val share: String
  val price: Long
  val count: Long
}

case class SellOrder(client: Client, share: String, price: Long, count: Long) extends Order

case class BuyOrder(client: Client, share: String, price: Long, count: Long) extends Order
