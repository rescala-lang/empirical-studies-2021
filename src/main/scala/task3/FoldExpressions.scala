package task3

import rescala.default._

import scala.util.Random

case class Frame(sourceAddress: Int, data: Array[Byte])

object FoldExpressions {

  val monitor: Evt[Frame] = Evt[Frame]
  val timer: Evt[Long] = Evt[Long]

  /* Assume you ingress events from the monitor mode of a wifi chip.
     The system should observe traffic per source address.
     Compute the following values (as signals) since the last time the timer triggered:
     • The average frame size for each source address
     • The number of frames for each source address
     • The total size of data per source address
     • The bytes per second per source address (the timer provides the passed time in milliseconds)

     When the timer triggers, output all values.

     Note: try using `Events.foldAll` to combine multiple events into a signal.
   */

}

object TestFold {

  import FoldExpressions._

  def main(args: Array[String]): Unit = {
    def randomBytes = Random.nextBytes(Random.nextInt(100))
    monitor.fire(Frame(1, randomBytes))
    monitor.fire(Frame(2, randomBytes))
    monitor.fire(Frame(1, randomBytes))
    println("one second has passed")
    timer.fire(1000)
    monitor.fire(Frame(1, randomBytes))
    monitor.fire(Frame(2, randomBytes))
    monitor.fire(Frame(2, randomBytes))
    monitor.fire(Frame(3, randomBytes))
    monitor.fire(Frame(1, randomBytes))
    monitor.fire(Frame(1, randomBytes))
    println("another two seconds have passed")
    timer.fire(2000)
  }
}
