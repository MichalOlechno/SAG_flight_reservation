package flight_reservation

import Flight.FlightDetails
import Flight.FlightNames.FlightNames
import akka.actor.{Actor, ActorRef, Props}
import flight_reservation.ReservationAgent.{MakeAReservation, SendFlightDetail}
import flight_reservation.sbt.util.Customer.SuitableFlights

import scala.collection.mutable.ListBuffer

object ReservationAgent {

  def props(): Props = Props()
  final case class MakeAReservation(flightNumber:String)
  final case class echo()
  final case class SendFlightDetail(FlightName:FlightNames)
}

class ReservationAgent() extends Actor
{
  val availableFlightsList : ListBuffer[FlightDetails] = ListBuffer()
  var rand=new scala.util.Random()
  var numberOfSeatsReserved:Int=0
  // we shouldn't have knowledge about customer here but we should pass sender() to directFlight (which is next agent)
  //var senderCutomer :ActorRef =null
   def receive = {
    case SendFlightDetail(destination)=>
      availableFlightsList.foreach(flight => if(flight.flightName==destination){
        sender() ! SuitableFlights(flight)
      })
     case MakeAReservation(flightNumber) =>
      /*

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


       */
   }


}

