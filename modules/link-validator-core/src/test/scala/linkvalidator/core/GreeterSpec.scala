package linkvalidator.core

import cats.{Applicative, Show}
import cats.effect.IO
import cats.effect.std.Console
import extras.hedgehog.ce3.syntax.runner.*
import hedgehog.*
import hedgehog.runner.*
import types.*

import java.nio.charset.Charset

object GreeterSpec extends Properties {
  override def tests: List[Test] = List(
    property("""Greeter(Greeting).greet(Name) should print out "message name!"""", testGreet)
  )

  def testGreet: Property = for {
    name    <- Gen.string(Gen.alpha, Range.linear(3, 10)).map(Name(_)).log("name")
    message <- Gen.string(Gen.unicode, Range.linear(5, 15)).map(Message(_)).log("message")
  } yield runIO {

    given console: ConsoleStub[IO] = ConsoleStub()

    val greeting = Greeting[IO](message)
    val greeter  = Greeter[IO](greeting)

    val expectedOutput = List(s"${message.value} ${name.value}!\n")

    greeter.greet(name).map { _ =>
      (console.getStoredOutput ==== expectedOutput).log(
        s"""console's stored output does not match expected output
           |    stored output: ${console.getStoredOutput}
           |  expected output: $expectedOutput
           |""".stripMargin
      )
    }
  }

  /** `ConsoleStub` is a stub for `Console[F]`. Instead of printing, it captures the input as a `String`.
    * @param AP Applicative. It is required to return `F[Unit]` instead of `Unit`.
    *           If you're not sure what to use, just do `ConsoleStub[IO]()`.
    * @tparam F The effect type that gives you an abstraction to attach additional functionality to `Console[F]`.
    *           e.g.)
    *
    *           - `[F[*]: Applicative]` gives you a way to create `F[Anything]` and more.
    *           - `[F[*]: Functor]` gives you a way to use the `map` method to transform the internal value.
    */
  private class ConsoleStub[F[*]: Applicative as AP]() extends Console[F] {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    private var storedOutput: Vector[String] = Vector.empty // scalafix:ok DisableSyntax.var

    def getStoredOutput: List[String] = storedOutput.toList

    @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
    override def readLineWithCharset(charset: Charset): F[String] = ???

    override def print[A](a: A)(using S: Show[A]): F[Unit] = {
      val aInString = S.show(a)
      storedOutput = storedOutput :+ aInString
      AP.unit // returns F[Unit]
    }

    override def println[A](a: A)(using S: Show[A]): F[Unit] = this.print(s"$a\n")

    @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
    override def error[A](a: A)(using S: Show[A]): F[Unit] = ???

    @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
    override def errorln[A](a: A)(using S: Show[A]): F[Unit] = ???
  }
}
