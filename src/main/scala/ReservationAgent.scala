package flight_reservation

import akka.actor.{Actor, ActorRef, Props}
import flight_reservation.ReservationAgent.MakeAReservation

object ReservationAgent {

  def props(printerActor: ActorRef): Props = Props(new ReservationAgent(printerActor))
  final case class MakeAReservation(flightNumber:String)
  final case class echo()

}

class ReservationAgent(printerActor: ActorRef) extends Actor
{
  import Printer._
  var rand=new scala.util.Random()
  var numberOfSeatsReserved:Int=0
  // we shouldn't have knowledge about customer here but we should pass sender() to directFlight (which is next agent)
  //var senderCutomer :ActorRef =null
   def receive = {
     case MakeAReservation(flightNumber) =>
       printerActor ! Print(s"Making a reservation for ${sender()} reserved on ${flightNumber} flight")

       //senderCutomer=sender()

       numberOfSeatsReserved=1+rand.nextInt((100-1)+1)
       if(numberOfSeatsReserved<80) {
         sender() ! "OK 1"
         //sender() ! Customer.ReservationOK(flightNumber,(1+rand.nextInt(100-1)+1).toString())
       }
         else
         sender() ! "Fauilure 1"
        //sender() ! Customer.ReservationFailed(flightNumber,numberOfSeatsReserved)
    case echo =>
       printerActor ! Print(s"${self} echo!!!!!!!!!!!!1")

   }


}

