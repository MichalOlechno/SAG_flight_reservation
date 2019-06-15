package flight_reservation

import akka.actor.{ActorRef, Cancellable}

import scala.collection.mutable.ListBuffer

class CustomerData(Actor:ActorRef,Reservations:ListBuffer[String]) {
  val reservations=Reservations
  val actor=Actor

}