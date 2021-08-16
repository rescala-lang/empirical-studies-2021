package task5

import org.scalajs.dom.UIEvent
import rescala.default._
import rescala.extra.lattices.delta.CContext._
import rescala.extra.lattices.delta.Delta
import rescala.extra.lattices.delta.crdt.reactive.RGA
import rescala.extra.lattices.delta.crdt.reactive.RGA._
import task5.Todolist.replicaId

import java.util.concurrent.ThreadLocalRandom

class TaskListHandler(toggleAll: Event[UIEvent], taskRefs: TaskRefObj) {

  type State = RGA[TaskRef, DietMapCContext]

  def listInitial: State = RGA[TaskRef, DietMapCContext](replicaId)

  def create(state: => State)(desc: String): State = {
    val taskid  = s"Task(${ThreadLocalRandom.current().nextLong().toHexString})"
    val taskref = taskRefs.lookupOrCreateTaskRef(taskid, Some(TaskData(desc)))
    state.resetDeltaBuffer().prepend(taskref)
  }

  def removeAll(state: => State, dt: DynamicTicket): State = {
    state.resetDeltaBuffer().deleteBy { taskref =>
      dt.depend(taskref.task).read.exists(_.done)
    }
  }

  def remove(state: => State)(id: String): State = {
    state.resetDeltaBuffer().deleteBy { taskref =>
      taskref.id == id
    }
  }

  def delta(s: => State)(delta: Delta[RGA.State[TaskRef, DietMapCContext]]): State = {
    val list = s

    val newList = list.resetDeltaBuffer().applyDelta(delta)

    val oldIDs = list.toList.toSet
    val newIDs = newList.toList.toSet

    val removed = oldIDs -- newIDs
    removed.foreach { _.task.disconnect() }

    newList
  }

}
