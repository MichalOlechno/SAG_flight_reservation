import akka.actor.{Actor, ActorRef, Props}
import flight_reservation.Printer

object FlightSupervisor {
  def props(printerActor: ActorRef,numOfFlghts:Int,numOfCustomers:Int,numOfReservationAgents:Int): Props = Props(new FlightSupervisor(printerActor,numOfFlghts,numOfCustomers,numOfReservationAgents))
  final case class CreateCustomers(number: Int)
  final case class CreateReservationAgents(number: Int)
  final case class ReserveASeat(flightNumber:String,reservationAgent:ActorRef,reservationAgent2:ActorRef)
}


class FlightSupervisor(printer:ActorRef, numOfFlghts:Int,numOfCustomers:Int,numOfReservationAgents:Int) extends Actor {
  import Printer._
  import FlightSupervisor._




  def receive={
    case CreateCustomers(number) =>
      printer ! Print(s"Creating ${number} Customers")

    case CreateReservationAgents(number) =>
      printer ! Print(s"Creating ${number} ReservationAgents")
    case ReserveASeat(flightNumber,reservationAgent,reservationAgent2)=>
      printer ! Print(s"${self} is sending a request to Reservation agent")
      ReserveASeat(flightNumber,reservationAgent,reservationAgent2 )
    case _ =>
  }
}

