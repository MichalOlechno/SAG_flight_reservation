package flight_reservation
package sbt.util

import Flight.{FlightDetails, FlightNames}
import Flight.FlightNames.FlightNames
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.example.AkkaQuickstart
import flight_reservation.FlightSupervisor.{CreateCustomers, GetAvailableReservationAgents}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Customer {
  def props(): Props = Props()
  //#greeter-messages
  final case class ReservationOK(flightNumber: String, seatNumber:String)
  final case class ReservationFailed(flightNumber:String,occupiedSeats:Int)
  final case class ReserveASeat(flightNumber:String,reservationAgent:ActorRef,reservationAgent2:ActorRef)
  final case class SearchForFlight(Destination:FlightNames)
  final case class SuitableFlights(flightDetails:FlightDetails)
}

class Customer() extends Actor{
  import Customer._
  import Printer._
  import ReservationAgent._
  implicit val timeout = Timeout(5 seconds)
  def receive = {
    case SearchForFlight(flightName)=>
      //TODO: Ask FlightSupervisor for available ReservationAgents and then send request for them
      val response =context.parent ? GetAvailableReservationAgents()
      val availableAgents= Await.result(response,timeout.duration).asInstanceOf[ListBuffer[ActorRef]]
      availableAgents.foreach(agent=>agent ! SendFlightDetail(flightName))
    case SuitableFlights(flightDetails)=>
      //TODO: Collect flightDetails from agents for specified amount of time

    case _ =>
  }

  def ReserveASeat(flightNumber:String, reservationAgent: ActorRef,reservationAgent2: ActorRef):Unit ={
    /*
    implicit val timeout = Timeout(5 seconds)
    //reservationAgent ? MakeAReservation(flightNumber)
    //val realReservationAgent = context.actorSelection("akka://TicketReservationSystem/user/ReservationAgent")
    val response = (reservationAgent? MakeAReservation(flightNumber))
    val result=Await.result(response,timeout.duration).asInstanceOf[String]
    printerActor ! Print(result)
    //(reservationAgent ? MakeAReservation(flightNumber))
    reservationAgent2 ! echo
     */
  }

}
