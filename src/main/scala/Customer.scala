package flight_reservation
package sbt.util

import Flight.{FlightDetails, FlightNames}
import Flight.FlightNames.FlightNames
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.example.AkkaQuickstart
import flight_reservation.FlightSupervisor.{CreateCustomers, GetAvailableReservationAgents, ReceiveCustomerData, ReservationDone, customerReservedAFlight}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Customer {
  def props(): Props = Props(new Customer())
  //#greeter-messages
  final case class ReservationOK(flightNumber: String, seatNumber:String)
  final case class ReservationFailed(flightNumber:String,occupiedSeats:Int)
  final case class ReserveASeat(flightNumber:String,reservationAgent:ActorRef,reservationAgent2:ActorRef)
  final case class SearchForFlight(Destination:FlightNames)
  final case class SuitableFlights(flightDetails:FlightDetails)
  final case class reservationDone(flightDetails:FlightDetails)
  final case class GetData()
  final case class Tick()
}

class Customer() extends Actor{
  import Customer._
  import ReservationAgent._
  implicit val timeout = Timeout(5 seconds)
  var reservedFlights: ListBuffer[Int] = ListBuffer()
  val log = Logging(context.system, this)
  var agent_status=""
  var availableAgents: ListBuffer[ActorRef] = ListBuffer()
  var answers:ListBuffer[FlightDetails]=ListBuffer()
  var start=System.nanoTime()
  var end=System.nanoTime()
  var seatsReserved: ListBuffer[ActorRef] = ListBuffer()
  def receive = {
    case SearchForFlight(flightName)=>
      val response =context.parent ? GetAvailableReservationAgents()
      availableAgents= Await.result(response,timeout.duration).asInstanceOf[ListBuffer[ActorRef]]
      availableAgents.foreach(agent=>agent ! SendFlightDetail(flightName))
      agent_status="waitingForAnswers"
      start=System.nanoTime()
    case SuitableFlights(flightDetails)=>
      answers +=flightDetails
    case reservationDone(flightDetails) =>
      if(flightDetails==null)
        {
          SelectAFlight()
        }
      else{
        reservedFlights+=flightDetails.flightID
        context.parent ! customerReservedAFlight(self)
      }
    case Tick() =>
      if(agent_status.equalsIgnoreCase("waitingForAnswers")) {
        end=System.nanoTime()
        if (answers.length == availableAgents.length || (end-start)>500*1000*1000 ){
            SelectAFlight()
        }
      }
    case GetData() =>
      context.parent ! ReceiveCustomerData(reservedFlights)
    case _ =>
  }

  def SelectAFlight(): Unit = {
    agent_status="ok"
    answers(0).agent ! ReservationAgent.MakeAReservation(answers(0))
    answers-=answers(0)
  }
}
