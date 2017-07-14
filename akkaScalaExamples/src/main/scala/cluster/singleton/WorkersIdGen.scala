package cluster.singleton

import akka.actor.Props
import akka.persistence.PersistentActor

/**
  * Created by maria.forero on 14/07/2017.
  */

object WorkersIdGen
{
    case object GeneratedId
    case class IdGenerated(id : Int)

    def props() = Props(new WorkersIdGen)
}

class WorkersIdGen extends PersistentActor{

  import WorkersIdGen._

  override val persistenceId : String = "WorkersIdGen"

  private var currentId = 0


  override def receiveRecover: Receive = {
    case IdGenerated(id) =>
      currentId = id
  }

  override def receiveCommand: Receive = {
    case GeneratedId =>
      persist(IdGenerated(currentId + 1)){
        evt => currentId = evt.id
          sender() ! evt
      }
  }
}
