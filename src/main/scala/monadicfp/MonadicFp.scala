package monadicfp

import scalaz._, Scalaz._
import com.typesafe.config.Config
import org.jboss.netty.handler.codec.http.{
  HttpRequest, DefaultHttpRequest, HttpVersion, HttpMethod
}

object MonadicFp {
  object A_B {
    object reader {
      def conf_version(conf: Config): HttpVersion = {
        ???
      }

      def conf_method(conf: Config): HttpMethod = {
        ???
      }

      def uri_conf_request(uri: String) = (conf: Config) => {
        uri
      }

      def versionR = Reader[Config, HttpVersion](conf_version)

      def methodR = Reader[Config, HttpMethod](conf_method)

      def uriR(uri: String) = Reader[Config, String](uri_conf_request(uri))

      def tryout(uri: String) = {
        for {
          v <- versionR
          m <- methodR
          u <- uriR(uri)
        } yield {
          new DefaultHttpRequest(v, m, u)
        }
      }
    }
  }

  object A_A {
    object endo {
      def f(a: Int): Int = a

      def tryout {
        val g = Endo(f)
        val h = g |+| g
        h(3)
      }

      def tryout2 {
        val g = f _ |+| f _
        g(3)
      }
    }
  }

  object MA_NA {
    object nutural_transformation {
      val toList = new (Option ~> List) {
        def apply[T](opt: Option[T]): List[T] = {
          opt.toList
        }
      }

      toList(3.some)
      toList(true.some)
    }
  }

  object operational_monad {
    // http://stackoverflow.com/questions/25403944/using-free-with-a-non-functor-in-scalaz
    sealed trait Console[_]
    case class PrintLine(msg: String) extends Console[Unit]
    case object ReadLine extends Console[String]
//    type ConsoleCoyo[A] = Coyoneda[Console, A]
//    type ConsoleMonad[A] = Free.FreeC[ConsoleCoyo, A]
    Free.liftFC(ReadLine)
  }

  object A_MB {
    object kleisli {
      def even(a: Int): Option[Int] = if (a % 2 == 0) Some(a) else None
      val a = Kleisli(even)
      val f = a >=> a >=> a
      f(1)
    }
    object kleisli_endomorphic {
      // http://d.hatena.ne.jp/xuwei/20131118/1384797218
    }
  }
}
