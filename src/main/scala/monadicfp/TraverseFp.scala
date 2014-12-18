package monadicfp

import scalaz._, Scalaz._

object TraverseFp {
  def state {
    val xs = List(1, 2, 3)
    val s = xs.traverseS(x => State((index: Int) => {
      val i = index + 1
      (i, (i, x))
    }))
    s.run(0)
  }

  def state2 {
    val xs = List(1, 2, 3)
    xs.runTraverseS(0)(x => State((index: Int) => {
      val i = index + 1
      (i, (i, x))
    }))
  }

  def foldLeft {
    val xs = List(1, 2, 3)
    xs.foldLeft((0, List.empty[Int])) { (z, x) =>
      val i = z._1 + 1
      (i, z._2 :+ x)
    }
  }

  def monad {
    val xs = List(1, 2, 3)
    xs.traverseM(x => Option(List(x + 1)))
  }

  // traverseU
}
