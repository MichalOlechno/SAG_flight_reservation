import akka.actor.{Actor, ActorRef, Props}


object Flight {
  def props(printerActor: ActorRef,flightNumber:String): Props = Props(new Flight(printerActor,flightNumber))
  final case class BookASeat(seatNumber : Int)
}


class Flight(printer:ActorRef, flightNumber:String) extends Actor {
  import Flight._
  var seatsList=new Array[String](100)
  var seatsTaken=0

  def receive={
    case BookASeat(seatNumber : Int) =>
      if (seatsTaken<seatsList.length)
        {
          seatsList(seatsTaken)=sender().toString()
          seatsTaken=seatsTaken+1
          sender ! s"Reserved a seat number $seatsTaken on flight number $flightNumber"
        }
      else
        {
          sender ! s"Reservation Failed on fliht number $flightNumber"
        }
    case _ =>

  }
}
