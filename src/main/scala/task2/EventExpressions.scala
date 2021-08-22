package task2

import rescala.default._

import scala.collection.immutable.Queue

/*
Task2:

REScala has event expressions `Event { }` as a generic mechanism to declare events, but also provides many specific combinators for often used functionality.
Event expressions can be used to define the same semantics as combinators.

Your task is to state which combinators (c1-c13) correspond to which event expressions (d1-d13).
There are also 4 extra expressions (e1-e4) that are alternative forms of the above, also assign those.

You may use `assertEquals(cN, dN)` to test you assumption.
 */

object EventExpressions {

  val a = Evt[String]
  val b = Evt[String]

  val s0 = Evt[String]
  val s1 = Var("init s1")
  val s2 = Var("init s2")

  val v1 = Var("Initial")
  val v2 = Var(v1)

  val n = 5

  val c1  = a || b
  val c2  = a \ b
  val c3  = a zip b
  val c4  = a zipOuter b
  val c5  = a.dropParam
  val c6  = a.filter(_.startsWith("x"))

  val c7  = s0.count()
  val c8  = s0.latest("init")
  val c9  = s0.latestOption()
  val c10 = s0.list()
  val c11 = s0.last(n)
  val c12 = s0.toggle(s1, s2)
  val c13 = v2.flatten

  val d1  = Event { if (a.value.isDefined && a.value.get.startsWith("x")) a.value else None }
  val d2  = Event { a.value zip b.value }
  val d3  = Event { if (a.value.isDefined) a.value else b.value }
  val d4  = Event { if (a.value.isDefined || b.value.isDefined) Some(a.value -> b.value) else None }
  val d5  = Event { if (b.value.isEmpty) a.value else None }
  val d6  = Event { if (a.value.isDefined) Some(()) else None }

  val d7  = s0.fold(List.empty[String]) { (list, v) => v :: list }
  val d8  = s0.fold(Option.empty[String])((_, v) => Some(v))
  val d9  = s0.fold(0)((s, _) => s + 1)
  val d10 = s0.fold(Queue.empty[String]) { (queue, v) =>
    if (queue.length >= n) queue.tail.enqueue(v) else queue.enqueue(v)
  }
  val d11 = s0.fold("init")((_, v) => v)
  val d12 = { val intermediate = s0.iterate(true)(v => !v); Signal { if (intermediate.value) s1.value else s2.value } }
  val d13 = Signal.dynamic { v2.value.value }

  val e1  = Event { a.value.filter(_.startsWith("x")) }
  val e2  = a.map(_ => ())
  val e3  = Event { a.value.orElse(b.value) }

  val e4  = s0.iterate(0)(s => s + 1)

  def assertEquals[T >: Null](c: Event[T], d: Event[T]): Unit = {
    var count = 2
    var ct: T = null
    var dt: T = null
    def check = {
      count -= 1
      if (count == 0) {
        if (ct == dt) println("OK")
        else println(s"events differ:\nevent 1: $c\nevent 2:$d")
        count = 2
      }
    }
    c.observe { v => ct = v; check }
    d.observe { v => dt = v; check }
  }

  def assertEquals[T >: Null](c: Signal[T], d: Signal[T]): Unit = {
    var count = 2
    var ct: T = null
    var dt: T = null
    def check = {
      count -= 1
      if (count == 0) {
        if (ct == dt) println("OK2")
        else println(s"signals differ:\nsignal 1: $c\nsignal 2:$d")
        count = 2
      }
    }
    c.observe { v => ct = v; check }
    d.observe { v => dt = v; check }
  }

  def main(args: Array[String]): Unit = {
    a.fire("hi")
    b.fire("ho")
    transaction(a, b) { implicit t =>
      a.fire("trans hi")
      b.fire("trans ho")
    }
    a.fire("hi 2")
    a.fire("hi 3")

    s0.fire("ev 1")
    s0.fire("ev 2")
    s1.set("change 1")
    s2.set("change 2")
    s0.fire("ev 3")
    s0.fire("ev 4")
    s1.set("change 3")
    s0.fire("ev 5")

  }

}
