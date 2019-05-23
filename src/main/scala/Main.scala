package com.example

import akka.actor.{ActorRef, ActorSystem}
import flight_reservation.FlightSupervisor
import flight_reservation.FlightSupervisor.{CreateCustomers, CreateReservationAgents}


object AkkaQuickstart extends App {
  val CustomerNumber=200
  val ReservationAgentNumber=200


  val system: ActorSystem=ActorSystem("TicketReservationSystem")
  val AgentsSupervisor:ActorRef=system.actorOf(FlightSupervisor.props(),name="AgentsSupervisor")

  AgentsSupervisor ! CreateCustomers(CustomerNumber)
  AgentsSupervisor ! CreateReservationAgents(ReservationAgentNumber)

  /*

  val customer:ActorRef=system.actorOf(Customer.props(printer),name="customerAgent")
  val reservationAgent:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent")
  val reservationAgent2:ActorRef=system.actorOf(ReservationAgent.props(printer),name="ReservationAgent2")

  for(i<- 1 to CustomerNumber) Customers += system.actorOf(Customer.props(printer),name=s"customerAgent${i}")
  for(i<- 1 to ReservationAgentNumber) ReservationAgents += system.actorOf(ReservationAgent.props(printer),name=s"ReservationAgent${i}")



  Customers.foreach(customer=>customer ! ReserveASeat("Tokyo-Warsaw",ReservationAgents(0),ReservationAgents(1)))
*/
}