//#full-example
package com.example

import org.scalatest.{ BeforeAndAfterAll, WordSpecLike, Matchers }
import akka.actor.ActorSystem
import akka.testkit.{ TestKit, TestProbe }
import scala.concurrent.duration._
import scala.language.postfixOps
//#test-classes
class AkkaQuickstartSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {
  //#test-classes

  def this() = this(ActorSystem("AkkaQuickstartSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  //#first-test
  //#specification-example
  "A Greeter Actor" should {
    "pass on a greeting message when instructed to" in {
      //#specification-example
      val testProbe = TestProbe()
      val helloGreetingMessage = "hello"

    }
  }
  //#first-test
}
//#full-example
