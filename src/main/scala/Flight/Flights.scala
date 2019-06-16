package Flight

object FlightNames extends Enumeration {
  type FlightNames =Value
  val Tokyo,NewYork,Cairo,Helsinki,Berlin=Value

  def parse(i:Int):FlightNames= {
    if (i == 0) {
      Tokyo
    }
    if (i == 1) {
      NewYork
    }
    if (i == 2) {
      Cairo
    }
    if (i == 3) {
      Helsinki
    }
    if (i == 4) {
      Berlin
    }
    else null
  }
}
