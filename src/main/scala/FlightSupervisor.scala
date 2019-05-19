package flight_reservation
import Flight.Flights
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.SearchForFlight

import scala.collection.mutable.ListBuffer

object FlightSupervisor {
  def props(): Props = Props()
  final case class CreateCustomers(number: Int)
  final case class CreateReservationAgents(number: Int)
  final case class StartReservation()
  final case class ReservationDone()
  final case class GetAvailableReservationAgents()
}


class FlightSupervisor() extends Actor {

  import FlightSupervisor._
  val log = Logging(context.system, this)
  var Customers: ListBuffer[ActorRef] = ListBuffer()
  var ReservationAgents: ListBuffer[ActorRef] = ListBuffer()
  var AvailableReservationAgents: ListBuffer[ActorRef] = ListBuffer()


  def receive={
    case CreateCustomers(number) =>
      for(i<- 1 to number) Customers += context.system.actorOf(Customer.props(),name=s"customerAgent${i}")
    case CreateReservationAgents(number) =>
      for(i<- 1 to number) ReservationAgents += context.system.actorOf(ReservationAgent.props(),name=s"ReservationAgent${i}")
    case StartReservation() =>
      //TODO: if availableFlights >0 then RAND within that flights
      Customers.foreach(customer=>customer ! SearchForFlight(Flights.WarsawTokyo))
    case ReservationDone()=>
      //TODO: if availableFlight >0 then RAND within the flights
      sender() ! SearchForFlight(Flights.HelsinkiStockholm)
    case GetAvailableReservationAgents() =>
      sender() ! AvailableReservationAgents
    case _ =>
  }
}

