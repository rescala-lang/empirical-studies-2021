package task4

import rescala.extra.lattices.Lattice

case class Epoche[T](number: Int, payload: T)

object Epoche {

  implicit def makeEpocheInstance[T: Lattice]: Lattice[Epoche[T]] = new Lattice[Epoche[T]] {
    override def merge(left: Epoche[T], right: Epoche[T]): Epoche[T] = {
      if (left.number > right.number) left
      else if (left.number < right.number) right
      else Epoche(number = left.number, payload = Lattice[T].merge(left.payload, right.payload))
    }
  }

}
