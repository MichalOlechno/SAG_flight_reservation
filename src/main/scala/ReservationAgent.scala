package flight_reservation

import Flight.{FlightDetails, FlightNames}
import Flight.FlightNames.FlightNames
import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import flight_reservation.FlightSupervisor.ReceiveReservationAgentData
import flight_reservation.ReservationAgent.{MakeAReservation, PrepareData, SendFlightDetail}
import flight_reservation.sbt.util.Customer.{SuitableFlights, reservationDone}

import scala.collection.mutable.ListBuffer

object ReservationAgent {

  def props(parent:ActorRef): Props = Props(new ReservationAgent(parent:ActorRef))
  final case class MakeAReservation(flightDetails:FlightDetails)
  final case class echo()
  final case class SendFlightDetail(FlightName:FlightNames)
  final case class PrepareData(FlightNames:FlightNames)
}

class ReservationAgent(supervisor:ActorRef) extends Actor
{
  val availableFlightsList : ListBuffer[FlightDetails] = ListBuffer()
  var rand=new scala.util.Random()
  var numberOfSeatsReserved:Int=0
  val log = Logging(context.system, this)

   def receive = {
     case PrepareData(flightName) =>
       prepareData(flightName)
     case SendFlightDetail(destination) =>
       availableFlightsList.foreach(flight => if (flight.flightName == destination) {
         sender() ! SuitableFlights(flight)
       })
     case MakeAReservation(flightDetails) =>
       var flight: FlightDetails=null
       try {
         flight = availableFlightsList.find(x => x.flightName == flightDetails.flightName).get
         flight.seatsLeft = flight.seatsLeft - 1
         flight.seat += sender()
         sender() ! flight
         if (flight.seatsLeft < 1) {
           log.info("All seats taken")
           availableFlightsList -= flight
           if (availableFlightsList.isEmpty) {
             supervisor ! ReceiveReservationAgentData(flight)
           }
         }
       }
         catch{
           case e:NoSuchElementException=>

             sender() ! new FlightDetails("",0,0,0,null,null,"")
         }

     case _ =>
   }
  def prepareData(flightName:FlightNames):Unit={
    availableFlightsList+=new FlightDetails("today",100,10,60,flightName,self,self.toString())
    availableFlightsList+=new FlightDetails("today",100,10,60,flightName,self,self.toString())
  }
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

