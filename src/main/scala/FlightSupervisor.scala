package flight_reservation

import Flight.{FlightDetails, FlightNames}
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.{GetData, SearchForFlight, Tick}

import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

object FlightSupervisor {
  def props(): Props = Props(new FlightSupervisor())
  final case class CreateCustomers(number: Int)
  final case class CreateReservationAgents(number: Int)
  final case class CreateTickAgent()
  final case class StartReservation()
  final case class ReservationDone()
  final case class GetAvailableReservationAgents()
  final case class schedule()
  final case class TickACustomer()
  final case class customerReservedAFlight(agent:ActorRef)
  final case class ReceiveReservationAgentData(flightDetails:FlightDetails)
  final case class ReceiveCustomerData(reservedFlights:ListBuffer[Int])

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
      StartReservation()
    case customerReservedAFlight(agent)=>
      StartReservation(agent)
    case GetAvailableReservationAgents() =>
      sender() ! AvailableReservationAgents
    case schedule() =>
      implicit val ec: ExecutionContext = context.dispatcher
     Customers.foreach(x=>context.system.scheduler.schedule(0 seconds,100 milliseconds){
       x ! Tick()
     })
    case ReceiveReservationAgentData(flightDetails)=>
      //TODO: Get Date
    case ReceiveCustomerData(reservedFlights)=>
    //TODO: Get Date
    case _ =>
  }

  def StartReservation():Unit={
    //TODO: rand flightNames
    Customers.foreach(customer=>customer ! SearchForFlight(FlightNames.Berlin))
  }
  def StartReservation(agent:ActorRef):Unit={
    if (AvailableReservationAgents.length>0)
      {
        //TODO: rand flightNames
        agent ! SearchForFlight(FlightNames.Berlin)
      }
    else agent ! GetData()
  }
}

