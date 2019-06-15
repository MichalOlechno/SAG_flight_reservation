package Flight

import Flight.FlightNames.FlightNames
import akka.actor.ActorRef

import scala.collection.mutable.ListBuffer

class FlightDetails (FlightDate:String, price:Int,SeatsLeft:Int,FlightTime:Float,FlightName: FlightNames,Agent: ActorRef,FlightID:String)
  {
    var flightDate=FlightDate
    var seatsLeft=SeatsLeft
    var flightTime=FlightTime
    var flightName=FlightName
    var agent=Agent
    var flightID=FlightID
    var seat:ListBuffer[ActorRef]=ListBuffer()

  }

