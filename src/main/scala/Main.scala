package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import flight_reservation.{Printer, ReservationAgent}
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.ReserveASeat


object AkkaQuickstart extends App {
  val system: ActorSystem=ActorSystem("TicketReservationSystem")
  val printer:ActorRef=system.actorOf(Printer.props,"printerAgent")
  val customer:ActorRef=system.actorOf(Customer.props(printer),name="customerAgent")
  val reservationAgent:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent")
  val reservationAgent2:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent2")

  customer ! ReserveASeat("Warsaw-Tokyo",reservationAgent,reservationAgent2)

}