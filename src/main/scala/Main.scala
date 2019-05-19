package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import flight_reservation.{Printer, ReservationAgent}
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.ReserveASeat

import scala.collection.mutable.ListBuffer


object AkkaQuickstart extends App {
  val CustomerNumber=200
  val ReservationAgentNumber=200
  var Customers: ListBuffer[ActorRef] = ListBuffer()
  var ReservationAgents: ListBuffer[ActorRef] = ListBuffer()

  val system: ActorSystem=ActorSystem("TicketReservationSystem")
  val printer:ActorRef=system.actorOf(Printer.props,"printerAgent")
 /*
  val customer:ActorRef=system.actorOf(Customer.props(printer),name="customerAgent")
  val reservationAgent:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent")
  val reservationAgent2:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent2")
*/
  for(i<- 1 to CustomerNumber) Customers += system.actorOf(Customer.props(printer),name=s"customerAgent${i}")
  for(i<- 1 to ReservationAgentNumber) ReservationAgents += system.actorOf(ReservationAgent.props(printer),name=s"ReservationAgent${i}")



  Customers.foreach(customer=>customer ! ReserveASeat("Tokyo-Warsaw",ReservationAgents(0),ReservationAgents(1)))

}