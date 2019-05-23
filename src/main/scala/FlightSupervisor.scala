package flight_reservation

import Flight.{CustomerScheduler, FlightDetails, FlightNames}
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import flight_reservation.ReservationAgent.PrepareData
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
  final case class ReceiveCustomerData(reservedFlights:ListBuffer[String])
  final case class Stop(actor:ActorRef)

}


class FlightSupervisor() extends Actor {

  import FlightSupervisor._
  val log = Logging(context.system, this)
  var Customers: ListBuffer[ActorRef] = ListBuffer()
  var ReservationAgents: ListBuffer[ActorRef] = ListBuffer()
  var AvailableReservationAgents: ListBuffer[ActorRef] = ListBuffer()
  var customerScheduler:ListBuffer[CustomerScheduler]=ListBuffer()


  def receive={
    case CreateCustomers(number) =>
      for(i<- 1 to number) Customers += context.system.actorOf(Customer.props(self),name=s"customerAgent${i}")
    case CreateReservationAgents(number) =>
      for(i<- 1 to number)
        ReservationAgents += context.system.actorOf(ReservationAgent.props(self), name = s"ReservationAgent${i}")
        ReservationAgents.foreach(x=>x!PrepareData(FlightNames.Berlin))
        ReservationAgents.foreach(x=>AvailableReservationAgents+=x)
    case StartReservation() =>
      StartReservation()
    case customerReservedAFlight(agent)=>
      StartReservation(agent)
    case GetAvailableReservationAgents() =>
      sender() ! AvailableReservationAgents
    case schedule() =>
      implicit val ec: ExecutionContext = context.dispatcher
     Customers.foreach(x=> {val cancellable= context.system.scheduler.schedule(0 seconds,100 milliseconds){

       x ! Tick()
     }
       customerScheduler += new CustomerScheduler(x,cancellable)
     })
    case ReceiveReservationAgentData(flightDetails)=>
      AvailableReservationAgents-=sender()
      //TODO: Get Date
    case ReceiveCustomerData(reservedFlights)=>
    //TODO: Get Date
    case Stop(actor)=>
      try{
        customerScheduler.find(x=>x.actor==actor).get.cancellable.cancel()
        context.system.stop(actor)
      }
      catch{
        case e:NoSuchElementException =>
          context.system.stop(actor)
      }

    case _ =>
  }

  def StartReservation():Unit={
    //TODO: rand flightNames
    Customers.foreach(customer=>customer ! SearchForFlight(FlightNames.Berlin))
    log.info("ReservationStarted")

  }
  def StartReservation(agent:ActorRef):Unit={
    if (AvailableReservationAgents.length>0)
      {
        //TODO: rand flightNames
        agent ! SearchForFlight(FlightNames.Berlin)
        log.info("SearchForNextFlight")

      }
    else agent ! GetData()
  }
}

