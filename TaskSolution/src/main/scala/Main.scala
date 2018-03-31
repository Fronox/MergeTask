import java.io.{BufferedWriter, FileWriter}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import Shares._

object Main {
  var clients: ListBuffer[Client] = ListBuffer[Client]()//HashMap name->Client?
  var sellOrders: ListBuffer[SellOrder] = ListBuffer[SellOrder]()
  var buyOrders: ListBuffer[BuyOrder] = ListBuffer[BuyOrder]()

  def readClients(fileName: String): Unit = {
    val clientsFile = Source.fromFile(fileName)
    clientsFile.getLines().foreach{
      line =>
        val splittedLine = line.split("\t")
        val clientName = splittedLine(0)
        val clientBalance = splittedLine(1).toLong
        val clientACount = splittedLine(2).toLong
        val clientBCount = splittedLine(3).toLong
        val clientCCount = splittedLine(4).toLong
        val clientDCount = splittedLine(5).toLong
        clients += Client(clientName, clientBalance, mutable.HashMap[String, Long](
          A -> clientACount,
          B -> clientBCount,
          C -> clientCCount,
          D -> clientDCount
        ))
    }
    clientsFile.close()
  }

  def readOrders(fileName: String): Unit = {
    val ordersFile = Source.fromFile(fileName)
    ordersFile.getLines().foreach{
      line =>
        val splittedLine = line.split("\t")
        val clientOpt = clients.find(c => c.name == splittedLine(0))
        clientOpt match {
          case None =>
          case Some(client) =>
            val orderType = splittedLine(1)
            val shareName = splittedLine(2)
            val sharePrice = splittedLine(3).toLong
            val shareAmount = splittedLine(4).toLong
            orderType match {
              case "s" => sellOrders += SellOrder(client, shareName, sharePrice, shareAmount)
              case "b" => buyOrders += BuyOrder(client, shareName, sharePrice, shareAmount)
            }
        }
    }
  }

  def matchOrders(sellOrders: ListBuffer[SellOrder], buyOrders: ListBuffer[BuyOrder],
                  orderParameters: (String, Long, Long)): Unit = {
    /*sellOrders.foreach{
      sellOrder =>
        if(buyOrders.nonEmpty){
          val buyOrder = buyOrders.head
          val sellShare = orderParameters._1
          val sellPrice = orderParameters._2
          val sellAmount = orderParameters._3
          sellOrder.client.changeBalance(sellPrice * sellAmount)
          sellOrder.client.changeShares(sellShare, - sellAmount)
          buyOrder.client.changeBalance(- sellPrice * sellAmount)
          buyOrder.client.changeShares(sellShare, sellAmount)
        }
    }*/
    val sellShare = orderParameters._1
    val sellPrice = orderParameters._2
    val sellAmount = orderParameters._3
    sellOrders.zip(buyOrders).foreach{
      ordersPair =>
        val sellOrder = ordersPair._1
        val buyOrder = ordersPair._2
        sellOrder.client.changeBalance(sellPrice * sellAmount)
        sellOrder.client.changeShares(sellShare, - sellAmount)
        buyOrder.client.changeBalance(- sellPrice * sellAmount)
        buyOrder.client.changeShares(sellShare, sellAmount)
    }
  }

  def matching: Unit = {
    val groupedSellOrders = sellOrders.groupBy(order => (order.share, order.price, order.count))
    val groupedBuyOrders = buyOrders.groupBy(order => (order.share, order.price, order.count))
    groupedSellOrders.foreach{
      case (sellParameters, listSellOrders) =>
        val listBuyOrdersOpt = groupedBuyOrders.get(sellParameters)
        listBuyOrdersOpt match {
          case None =>
          case Some(listBuyOrders) => matchOrders(listSellOrders, listBuyOrders, sellParameters)
        }
    }
  }

  def writeResult(fileName: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(fileName))
    clients foreach {client => writer.write(client.toString); writer.newLine()}
    writer.close()
  }

  def readData(clientsFile: String, ordersFile: String): Unit = {
    readClients(clientsFile)
    readOrders(ordersFile)
  }

  def main(args: Array[String]): Unit = {
    //readClients(args(0))
    /*val r1 = clients.map(client => List(client.shares(A), client.shares(B), client.shares(C), client.shares(D)))
      .foldLeft(List(0l,0l,0l,0l))((acc, elem) => acc.zip(elem).map(pair => pair._1 + pair._2))
    println(r1)*/
    //readOrders(args(1))
    readData(args(0), args(1))
    matching
    /*val r2 = clients.map(client => List(client.shares(A), client.shares(B), client.shares(C), client.shares(D)))
      .foldLeft(List(0l,0l,0l,0l))((acc, elem) => acc.zip(elem).map(pair => pair._1 + pair._2))
    println(r2)*/
    writeResult(args(2))
  }


}
