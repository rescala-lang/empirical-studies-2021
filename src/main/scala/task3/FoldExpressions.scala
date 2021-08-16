package task3

import rescala.default._

case class Frame(sourceAddress: Int, data: Array[Byte])

object FoldExpressions {

  val monitor: Event[Frame] = Evt[Frame]
  val startObservingAddress: Event[Int] = Evt[Int]
  val stopObservingAddress: Event[Int] = Evt[Int]
  val timer: Event[Long] = Evt[Long]

  /* Assume you ingress events from the monitor mode of a wifi chip.
     The system should observe traffic for every source address in between matching start/stop events.
     Compute the following values (as signals) resetting every time the timer triggers
     • The average frame size for each source address
     • The number of frames for each source address
     • The total size of data per source address

     When the timer triggers, output all three values.
   */


  // Events.foldAll

}
