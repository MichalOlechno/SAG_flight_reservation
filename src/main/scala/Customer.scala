package flight_reservation
package sbt.util

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._

import scala.concurrent.{Await, Future}

object Customer {
  def props(printerActor: ActorRef): Props = Props(new Customer(printerActor))
  //#greeter-messages
  final case class ReservationOK(flightNumber: String, seatNumber:String)
  final case class ReservationFailed(flightNumber:String,occupiedSeats:Int)
  final case class ReserveASeat(flightNumber:String,reservationAgent:ActorRef,reservationAgent2:ActorRef)
}

class Customer(printerActor: ActorRef) extends Actor{
  import Customer._
  import Printer._
  import ReservationAgent._

  def receive = {
    case ReservationOK(flightNumber, seatNumber) =>
      printerActor ! Print(s"OK Seat number ${seatNumber} reserved on ${flightNumber} flight")
    case ReservationFailed(flightNumber, occupiedSeats) =>
      printerActor ! Print(s"FAILURE during reservation of a seat in a ${flightNumber} flight. Reserved ${occupiedSeats} % of seats")
    case ReserveASeat(flightNumber,reservationAgent,reservationAgent2)=>
      printerActor ! Print(s"${self} is sending a request to Reservation agent")
      ReserveASeat(flightNumber,reservationAgent,reservationAgent2 )
    case _ => printerActor ! Print(s"Not recognized request from ${sender()}")
  }

  def ReserveASeat(flightNumber:String, reservationAgent: ActorRef,reservationAgent2: ActorRef):Unit ={
    implicit val timeout = Timeout(5 seconds)
    //reservationAgent ? MakeAReservation(flightNumber)
    //val realReservationAgent = context.actorSelection("akka://TicketReservationSystem/user/ReservationAgent")
    val response = (reservationAgent? MakeAReservation(flightNumber))
    val result=Await.result(response,timeout.duration).asInstanceOf[String]
    printerActor ! Print(result)
    //(reservationAgent ? MakeAReservation(flightNumber))
    reservationAgent2 ! echo

  }

}
