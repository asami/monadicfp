package monadicfp

import scala.language.implicitConversions
import scala.language.higherKinds
import scalaz._, Scalaz._
import scalaz.concurrent.Task

object FreeUtils {
  import Free._
//  def liftFC[S[_], A](s: S[A]): FreeC[S, A] = liftFU(Coyoneda lift s)

//  def toFree[T[_], A](ta: T[A]): Free.FreeC[T, A] =
  // def toFree[T[_], A](ta: T[A]) =
  //   Free.liftF[({type λ[α]=Coyoneda[T, α]})#λ, A](Coyoneda(ta))
}

object FreeInterpreter {
  sealed trait Console[_]
  case class PrintLine(msg: String) extends Console[Unit]
  case object ReadLine extends Console[String]

  def printLine(msg: String) = Free.liftFC(PrintLine(msg))
  def readLine = Free.liftFC(ReadLine)

  val interpreterId = new (Console ~> Id) {
    def apply[T](c: Console[T]): Id[T] = {
      c match {
        case PrintLine(msg) => println(msg)
        case ReadLine => scala.io.StdIn.readLine()
      }
    }
  }

  val interpreterTask = new (Console ~> Task) {
    def apply[T](c: Console[T]): Task[T] = Task(interpreterId.apply(c))
  }

  def run[T](f: Free.FreeC[Console, T]): T = {
    Free.runFC(f)(interpreterId)
  }

  def runTask[T](f: Free.FreeC[Console, T]): Task[T] = {
    Free.runFC(f)(interpreterTask)
  }
}

object FileIo {
  import java.io.File
  import scalax.io._

  sealed trait FileCommand[_]
  case class WriteString(file: File, content: String) extends FileCommand[Unit]
  case class ReadString(file: File) extends FileCommand[String]

  def writeString(file: File, content: String) =
    Free.liftFC(WriteString(file, content))
  def readString(file: File) =
    Free.liftFC(ReadString(file))

  val interpreter = new (FileCommand ~> Id) {
    def apply[T](c: FileCommand[T]): Id[T] = {
      c match {
        case WriteString(file, content) =>
          Resource.fromFile(file).write(content)
        case ReadString(file) =>
          Resource.fromFile(file).string
      }
    }
  }
}

object AppLanguage {
  import FreeInterpreter._
  import FileIo._

  sealed trait Language[_]
  case class ConsoleLanguage[T](cmd: Console[T]) extends Language[T]
  case class FileLanguage[T](cmd: FileCommand[T]) extends Language[T]

  val interpreter = new (Language ~> Id) {
    def apply[T](c: Language[T]): Id[T] = {
      c match {
        case ConsoleLanguage(cmd) =>
          FreeInterpreter.interpreterId(cmd)
        case FileLanguage(cmd) =>
          FileIo.interpreter(cmd)
      }
    }
  }

  def run[T](f: Free.FreeC[Language, T]): T = {
    Free.runFC(f)(interpreter)
  }

  // object Implicits {
  //   implicit def ConsoleWrapper[T](cmd: Free.FreeC[Console, T]): Free.FreeC[Language, T] = cmd.map(x => ConsoleLanguage(cmd))
  //   implicit def FileWrapper[T](cmd: Free.FreeC[FileCommand, T]): Free.FreeC[Language, T] = FileLanguage(cmd)
  // }
}

object App {
  import FreeInterpreter._

  def sample {
    val f = for {
      msg <- readLine
      _ <- printLine(msg)
    } yield Unit
    run(f)
    runTask(f).run
  }

  // import AppLanguage.Implicits._

  // def sample2 {
  //   val f: Free.FreeC[AppLanguage.Language, _] = for {
  //     msg <- ConsoleWrapper(readLine)
  //     _ <- printLine(msg)
  //   } yield Unit
  //   AppLanguage.run(f)
  // }
}
