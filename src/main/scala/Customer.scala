package flight_reservation
package sbt.util

import Flight.{FlightDetails, FlightNames}
import Flight.FlightNames.FlightNames
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.example.AkkaQuickstart
import flight_reservation.FlightSupervisor.{CreateCustomers, GetAvailableReservationAgents, ReceiveCustomerData, ReservationDone, StartReservation, Stop, customerReservedAFlight}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, TimeoutException}

object Customer {
  def props(parent:ActorRef): Props = Props(new Customer(parent:ActorRef))
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

class Customer(supervisor:ActorRef) extends Actor{
  import Customer._
  import ReservationAgent._
  implicit val timeout = Timeout(1 seconds)
  var reservedFlights: ListBuffer[String] = ListBuffer()
  val log = Logging(context.system, this)
  var agent_status=""
  var availableAgents: ListBuffer[ActorRef] = ListBuffer()
  var answers:ListBuffer[FlightDetails]=ListBuffer()
  var start=System.nanoTime()
  var end=System.nanoTime()
  var seatsReserved: ListBuffer[ActorRef] = ListBuffer()
  def receive = {
    case SearchForFlight(flightName)=>

      try {
        log.info(s"Trying to get availableAgents")
        val response = supervisor ? GetAvailableReservationAgents()
        availableAgents = Await.result(response, timeout.duration).asInstanceOf[ListBuffer[ActorRef]]
        availableAgents.foreach(agent => agent ! SendFlightDetail(flightName))
        agent_status = "waitingForAnswers"
        log.info(s"WaitingForAnswers. AvailableAgents ${availableAgents.length}")
      }
      catch {
        case e: TimeoutException =>{
          log.info("Exception cought")
          supervisor ! customerReservedAFlight(self)
        }
      }
      start=System.nanoTime()
    case SuitableFlights(flightDetails)=>
      //log.info(s"Got answer from ${sender()}")
      answers +=flightDetails
    case reservationDone(flightDetails) =>
      if(flightDetails==null)
        {
          log.info(s"CONFLICT!!!!!!!!!!!!!!!!!1")
          SelectAFlight()
        }
      else{
        log.info(s"reserved OK ")
        reservedFlights+=flightDetails.flightID
        answers.clear()
        supervisor ! customerReservedAFlight(self)
      }
    case Tick() =>
      //TODO : TEST how many % of answers gathered (check if timeout is long enough)
      //log.info("Tick")
      if(!agent_status.equalsIgnoreCase("") && availableAgents.isEmpty) supervisor ! Stop(self)
      if(agent_status.equalsIgnoreCase("waitingForAnswers")) {
          end = System.nanoTime()
          if (answers.length == availableAgents.length || (end - start) > 5000 * 1000 * 1000) {
            SelectAFlight()
        }
      }
    case GetData() =>
      supervisor ! ReceiveCustomerData(reservedFlights)
    case _ =>
  }

  def SelectAFlight(): Unit = {
    if(answers.nonEmpty) {
      val response = answers(0).agent ? ReservationAgent.MakeAReservation(answers(0))
      val result = Await.result(response, timeout.duration).asInstanceOf[FlightDetails]
      if (result.flightID.isEmpty()) {
        log.info(s"CONFLICT!!!!!!!!!!!!!!!!!1")
        answers -= answers(0)
        agent_status = "ok"
        SelectAFlight()
      }
      else {
        log.info(s"reserved OK ")
        reservedFlights += result.flightID
        answers.clear()
        supervisor ! customerReservedAFlight(self)
      }
    }
    }

}
