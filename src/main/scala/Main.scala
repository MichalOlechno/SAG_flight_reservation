//#full-example
package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import flight_reservation.{Printer, ReservationAgent}
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.ReserveASeat



//#main-class
object AkkaQuickstart extends App {
  val system: ActorSystem=ActorSystem("TicketReservationSystem")
  val printer:ActorRef=system.actorOf(Printer.props,"printerAgent")
  val customer:ActorRef=system.actorOf(Customer.props(printer),name="customerAgent")
  val reservationAgent:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent")
  val reservationAgent2:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent2")

  customer ! ReserveASeat("Warsaw-Tokyo",reservationAgent,reservationAgent2)


  /*
    import Greeter._

    // Create the 'helloAkka' actor system
    val system: ActorSystem = ActorSystem("helloAkka")

    //#create-actors
    // Create the printer actor
    val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

    // Create the 'greeter' actors
    val howdyGreeter: ActorRef =
      system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
    val helloGreeter: ActorRef =
      system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
    //val goodDayGreeter: ActorRef =
    //#create-actors

    //#main-send-messages
    howdyGreeter ! WhoToGreet("Akka")
    howdyGreeter ! Greet

    howdyGreeter ! WhoToGreet("Lightbend")
    howdyGreeter ! Greet

    helloGreeter ! WhoToGreet("Scala")
    helloGreeter ! Greet
    /*
    goodDayGreeter ! WhoToGreet("Play")3
    goodDayGreeter ! Greet
    //#main-send-messages
  */


   */

//#main-class
//#full-example
}