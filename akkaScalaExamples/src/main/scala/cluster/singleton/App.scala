package cluster.singleton

import akka.actor.{ActorSystem, PoisonPill}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}

/**
  * Created by maria.forero on 14/07/2017.
  */
object App extends App{

  implicit val system = ActorSystem("test")

  val singletonManager = system.actorOf(ClusterSingletonManager.props(
    singletonProps = WorkersIdGen.props(),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)),
    name = "WorkersIdGen"
  )

}
