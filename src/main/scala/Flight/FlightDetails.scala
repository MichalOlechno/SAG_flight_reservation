package Flight

import Flight.FlightNames.FlightNames

class FlightDetails (FlightDate:String, price:Int,SeatsLeft:Int,FlightTime:Float,FlightName: FlightNames)
  {
    var flightDate=FlightDate
    var seatsLeft=SeatsLeft
    var flightTime=FlightTime
    var flightName=FlightName
  }

