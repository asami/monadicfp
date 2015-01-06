package monadicfp.freecomposition

import scala.language.higherKinds
import scalaz._, Scalaz._

object ConsoleService {
  sealed trait ConsoleOperation[_]
  case class PrintLine(msg: String) extends ConsoleOperation[Unit]
  case object ReadLine extends ConsoleOperation[String]

  def printLine(msg: String) = Free.liftFC(PrintLine(msg))
  def readLine = Free.liftFC(ReadLine)

  val interpreter = new (ConsoleOperation ~> Id) {
    def apply[T](c: ConsoleOperation[T]): Id[T] = {
      c match {
        case PrintLine(msg) => println(msg)
        case ReadLine => scala.io.StdIn.readLine()
      }
    }
  }

  def run[T](f: Free.FreeC[ConsoleOperation, T]): T = {
    Free.runFC(f)(interpreter)
  }

  class Consoles[F[_]](implicit I: Inject[ConsoleOperation, F]) {
    def printLine(msg: String): Free.FreeC[F, Unit] = Free.liftFC(I.inj(PrintLine(msg)))
    def readLine: Free.FreeC[F, String] = Free.liftFC(I.inj(ReadLine))
  }

  object Consoles {
    implicit def instance[F[_]](implicit I: Inject[ConsoleOperation, F]): Consoles[F] = new Consoles[F]
  }
}

object AuthService {
  sealed trait AuthOperation[_]
  case class Login(user: String, password: String) extends AuthOperation[Unit]

  val interpreter = new (AuthOperation ~> Id) {
    def apply[T](c: AuthOperation[T]): Id[T] = {
      c match {
        case Login(login, password) => println(s"$login:$password")
      }
    }
  }

  def run[T](f: Free.FreeC[AuthOperation, T]): T = {
    Free.runFC(f)(interpreter)
  }

  class Auths[F[_]](implicit I: Inject[AuthOperation, F]) {
    def login(user: String, password: String): Free.FreeC[F, Unit] = Free.liftFC(I.inj(Login(user, password)))
  }

  object Auths {
    implicit def instance[F[_]](implicit I: Inject[AuthOperation, F]): Auths[F] = new Auths[F]
  }
}

object App {
  import ConsoleService._, AuthService._

  def program[F[_]](implicit C: Consoles[F], A: Auths[F]) = {
    import C._, A._
    for {
      password <- readLine
      _ <- login("user", password)
    } yield ()
  }
}
