package flight_reservation

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Printer {
  //#printer-messages
  def props: Props = Props[Printer]
  //#printer-messages
  final case class Greeting(greeting: String)
  final case class Print (message:String)
}
//#printer-messages
//#printer-companion

//#printer-actor
class Printer extends Actor with ActorLogging {
  import Printer._



  def receive = {
    case Greeting(greeting) =>

      log.info("Greeting received (from " + sender() + "): " + greeting)
    case Print(message)=>
    log.info(message)
  }
}
//#printer-actor

