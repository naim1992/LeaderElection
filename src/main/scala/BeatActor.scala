package upmc.akka.leader


import akka.actor._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait BeatMessage
case class Beat (id:Int) extends BeatMessage
case class BeatLeader (id:Int) extends BeatMessage

case class BeatTick () extends Tick

case class LeaderChanged (nodeId:Int)

class BeatActor (val id:Int) extends Actor {

     val time : Int = 150
     val father = context.parent
     var leader : Int = 0 // On estime que le premier Leader est 0
     val scheduler = context.system.scheduler

     implicit val timeout: Timeout = Timeout(5 seconds)

    def receive = {

         // Initialisation
        case Start => {
              Thread.sleep(1000)
             self ! BeatTick
        }

        // Objectif : prevenir tous les autres nodes qu'on est en vie
        case BeatTick => {
             scheduler.scheduleOnce(time milliseconds , self, BeatTick)
              if (this.id == this.leader) {
                  father ! Message ("I am the leader")
                  father ! BeatLeader (this.id)
             }

             father ! Beat(id)
        }

        case LeaderChanged (nodeId) =>
                    if(nodeId != leader)
                        this.leader = nodeId




    }

}
