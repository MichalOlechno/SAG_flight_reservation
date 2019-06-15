package flight_reservation

import Flight.{FlightDetails, FlightNames}
import akka.actor.{Actor, ActorRef, Cancellable, Props}
import akka.event.Logging
import flight_reservation.ReservationAgent.PrepareData
import flight_reservation.sbt.util.Customer
import flight_reservation.sbt.util.Customer.{GetData, SearchForFlight, Tick}

import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

object FlightSupervisor {
  def props(): Props = Props(new FlightSupervisor())
  final case class CreateCustomers(number: Int)
  final case class CreateReservationAgents(number: Int)
  final case class CreateTickAgent()
  final case class StartReservation()
  final case class ReservationDone()
  final case class GetAvailableReservationAgents()
  final case class schedule()
  final case class TickACustomer()
  final case class customerReservedAFlight(agent:ActorRef)
  final case class ReceiveReservationAgentData(flightDetails:FlightDetails,isUnavailable:Boolean)
  final case class ReceiveCustomerData(reservedFlights:ListBuffer[String])
  final case class Stop(actor:ActorRef)
  final case class ProcessData()

}


class FlightSupervisor() extends Actor {

  import FlightSupervisor._

  val log = Logging(context.system, this)
  var Customers: ListBuffer[ActorRef] = ListBuffer()
  var ReservationAgents: ListBuffer[ActorRef] = ListBuffer()
  var AvailableReservationAgents: ListBuffer[ActorRef] = ListBuffer()
  var customerScheduler: ListBuffer[CustomerScheduler] = ListBuffer()
  var customersData: ListBuffer[CustomerData] = ListBuffer()
  var flightsData: ListBuffer[FlightDetails] = ListBuffer()
  var GotData = 0

  def receive = {
    case CreateCustomers(number) =>
      for (i <- 1 to number) Customers += context.system.actorOf(Customer.props(self), name = s"customerAgent${i}")
    case CreateReservationAgents(number) =>
      for (i <- 1 to number)
        ReservationAgents += context.system.actorOf(ReservationAgent.props(self), name = s"ReservationAgent${i}")
      ReservationAgents.foreach(x => x ! PrepareData(FlightNames.Berlin))
      ReservationAgents.foreach(x => AvailableReservationAgents += x)
    case StartReservation() =>
      StartReservation()
    case customerReservedAFlight(agent) =>
      StartReservation(agent)
    case GetAvailableReservationAgents() =>
      sender() ! AvailableReservationAgents
    case schedule() =>
      implicit val ec: ExecutionContext = context.dispatcher
      Customers.foreach(x => {
        val cancellable = context.system.scheduler.schedule(0 seconds, 100 milliseconds) {

          x ! Tick()
        }
        customerScheduler += new CustomerScheduler(x, cancellable)
      })
    case ReceiveReservationAgentData(flightDetails, isUnavailable) =>
      if (isUnavailable) AvailableReservationAgents -= sender()
      flightsData += flightDetails
    case ReceiveCustomerData(reservedFlights) =>
      customersData += new CustomerData(sender(), reservedFlights)
      if (customersData.length == Customers.length) ProcessData()
    //context.system.stop(sender())
    case Stop(actor) =>
      try {
        customerScheduler.find(x => x.actor == actor).get.cancellable.cancel()
        log.info(s"GetData1 for ${actor}")
        actor ! GetData()
        // context.system.stop(actor)
      }
      catch {
        case e: NoSuchElementException =>
          log.info(s"GetData2 for ${actor}")
          actor ! GetData()
      }
    case ProcessData() =>
    //customersData.foreach(customer=>
    //flightsData.foreach(flight=>
    //var CustomerOccurenceInFlightDetails = 0
    // var FlightReferencesInCustomerDetails = 0
    //val customerID = customersData(0).actor
    // val flightID = customersData(0).reservations(0)
    // log.info(s"customerID= ${customerID}")
    //log.info(s"flightID= ${flightID}")
    //customersData(0).reservations.foreach(x => if (x == flightID) FlightReferencesInCustomerDetails = FlightReferencesInCustomerDetails + 1)
    // val flight = flightsData.find(x => x.flightID == flightID).get
    // flight.seat.foreach(x => if (x == customerID) CustomerOccurenceInFlightDetails = CustomerOccurenceInFlightDetails + 1)
    // log.info(s"flights reserved by customer = ${FlightReferencesInCustomerDetails}")
    // log.info(s"Customer References in flight = ${CustomerOccurenceInFlightDetails}")

    case _ =>
  }

  def StartReservation(): Unit = {
    //TODO: rand flightNames
    Customers.foreach(customer => customer ! SearchForFlight(FlightNames.Berlin))
    log.info("ReservationStarted")

  }

  def StartReservation(agent: ActorRef): Unit = {
    if (AvailableReservationAgents.length > 0) {
      //TODO: rand flightNames
      agent ! SearchForFlight(FlightNames.Berlin)
      log.info("SearchForNextFlight")

    }
    else {
      //GotData = GotData + 1
      log.info(s"GetData3 for ${agent}")
      agent ! GetData()
      log.info(s"GotData = ${GotData}")
    }
  }

  def ProcessData(): Unit  = {
    var CustomerOccurenceInFlightDetails = 0
    var FlightReferencesInCustomerDetails = 0
    var customerID: ActorRef = null
    //var flightID = ""
    var error = false
    //var flight:FlightDetails=null

    customersData.foreach(customer => {
      customerID = customer.actor
      customer.reservations.foreach(customerFlight => {
        customer.reservations.foreach(z => {
          if (customerFlight == z) FlightReferencesInCustomerDetails = FlightReferencesInCustomerDetails + 1
        })
        val flight = flightsData.find(x => x.flightID == customerFlight).get
        flight.seat.foreach(x => {
          if (x == customerID) CustomerOccurenceInFlightDetails = CustomerOccurenceInFlightDetails + 1
        })
        if (FlightReferencesInCustomerDetails != CustomerOccurenceInFlightDetails) {
          error = true
          log.info("ERROR")

        }
        log.info(s"FLoghtID = ${customerFlight}")
        log.info(s"CustomerID = ${customerID}")
        log.info(s"flights reserved by customer = ${FlightReferencesInCustomerDetails}")
        log.info(s"Customer References in flight = ${CustomerOccurenceInFlightDetails}")
        FlightReferencesInCustomerDetails=0
        CustomerOccurenceInFlightDetails=0
      })
    })
    if(error)
      log.error("!!!!!!!!!!!!!!!!!!!!!ERROR!!!!!!!!!!!!!!!!!!!!")
  }
}

   // customersData(0).reservations.foreach(x => {
    //  if (x == flightID) FlightReferencesInCustomerDetails = FlightReferencesInCustomerDetails + 1
      //log.info(s"a ${x}")
    //})
    //flightsData.foreach(x=>log.info(s"foreach ${x.flightID}"))
    //val flight = flightsData.find(x => x.flightID == flightID).get
   // flight.seat.foreach(x => {
    //  if (x == customerID) CustomerOccurenceInFlightDetails = CustomerOccurenceInFlightDetails + 1
    //  log.info(s"b ${x}")
    //})
    //if (FlightReferencesInCustomerDetails != CustomerOccurenceInFlightDetails) {
    //  error = true
    //  log.info("ERROR")
    //  log.info(s"FLoghtID = ${flightID}")
    //  log.info(s"CustomerID = ${customerID}")
    //  log.info(s"flights reserved by customer = ${FlightReferencesInCustomerDetails}")
    //  log.info(s"Customer References in flight = ${CustomerOccurenceInFlightDetails}")
    //}
  //}
//}
