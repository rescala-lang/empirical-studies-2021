package task2

import rescala.default._

import scala.collection.immutable.Queue

object EventExpressions {

  def main(args: Array[String]): Unit = {}


  def eventCombinators(): Unit = {
    val a = Evt[String]
    val b = Evt[String]

    {
      val c = a || b
      val d = Event { if(a.value.isDefined) a.value else b.value }
      val e = Event { a.value.orElse(b.value) }
    }

    {
      val c = a \ b
      val d = Event { if(b.value.isEmpty) a.value else None }
    }

    {
      val c = a zip b
      val d = Event { a.value zip b.value }
    }

    {
      val c = a zipOuter b
      val d = Event { if(a.value.isDefined || b.value.isDefined) Some(a.value -> b.value) else None}
    }

    {
      val c = a.dropParam
      val d = a.map(_ => ())
      val e = Event{ if (a.value.isDefined) Some(()) else None }
    }

    {
      val c = a.filter(_.startsWith("x"))
      val d = Event { a.value.filter(_.startsWith("x")) }
      val e = Event { if (a.value.isDefined && a.value.get.startsWith("x")) a.value else None}
    }
  }

  def foldCombinators(): Unit = {

    val a = Evt[String]

    {
      val c = a.count()
      val d = a.iterate(0)(s => s + 1)
      val e = a.fold(0)((s, _) => s + 1)
    }

    {
      val c = a.latest("init")
      val d = a.fold("init")((_, v) => v)
    }

    {
      val c = a.latestOption()
      val d = a.fold(Option.empty[String])((_, v) => Some(v))
    }

    {
      val c = a.list()
      val d = a.fold(List.empty[String]){ (list, v) => v :: list }
    }

    {
      val n = 5
      val c = a.last(n)
      val d = a.fold(Queue.empty[String]) {  (queue, v) =>
        if (queue.length >= n) queue.tail.enqueue(v) else queue.enqueue(v)
      }
    }

    {
      val s1 = Var("init s1")
      val s2 = Var("init s2")

      val c = a.toggle(s1, s2)
      val d = {
        val intermediate = a.iterate(true)(v => !v)
        Signal { if (intermediate.value) s1.value else s2.value}
      }
    }

  }

  def flattenCombinators(): Unit = {
    val a = Var("Initial")
    val b = Var(a)

    val c = b.flatten
    val d = Signal.dynamic { b.value.value }

    assert(c.now == "Initial")
  }

}
