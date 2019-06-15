package Flight

import akka.actor.{ActorRef, Cancellable}

class CustomerScheduler(Actor:ActorRef,task:Cancellable) {
  val cancellable=task
  val actor=Actor

}
