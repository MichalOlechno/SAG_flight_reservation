import akka.actor.{Actor, ActorRef, Props}

object FlightSupervisor {
  def props(printerActor: ActorRef,numOfFlghts:Int,numOfCustomers:Int,numOfReservationAgents:Int): Props = Props(new FlightSupervisor(printerActor,numOfFlghts,numOfCustomers,numOfReservationAgents))

}


class FlightSupervisor(printer:ActorRef, numOfFlghts:Int,numOfCustomers:Int,numOfReservationAgents:Int) extends Actor {
  import FlightSupervisor._

  def receive={

    case _ =>
  }
}
