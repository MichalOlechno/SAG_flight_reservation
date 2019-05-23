package com.example

import akka.actor.{ActorRef, ActorSystem}
import flight_reservation.FlightSupervisor
import flight_reservation.FlightSupervisor.{CreateCustomers, CreateReservationAgents, StartReservation, TickACustomer, schedule}


object AkkaQuickstart extends App {
  val CustomerNumber=200
  val ReservationAgentNumber=200


  val system: ActorSystem=ActorSystem("TicketReservationSystem")
  val AgentsSupervisor:ActorRef=system.actorOf(FlightSupervisor.props(),name="AgentsSupervisor")

  AgentsSupervisor ! CreateCustomers(CustomerNumber)
  AgentsSupervisor ! CreateReservationAgents(ReservationAgentNumber)
  AgentsSupervisor ! schedule()
  AgentsSupervisor ! StartReservation()

}