package linkvalidator.core

import cats.effect.IO
import extras.hedgehog.ce3.syntax.runner.*
import hedgehog.*
import hedgehog.runner.*
import types.*

object GreetingSpec extends Properties {
  override def tests: List[Test] = List(
    property("""Greeting(Message).get(Name) should return "message name"""", testGreet),
    property("test add", testGreet2),
    example("It should always return 0", testSomething)
  )

  def testGreet: Property = for {
    name    <- Gen.string(Gen.alpha, Range.linear(3, 10)).map(Name(_)).log("name")
    message <- Gen.string(Gen.unicode, Range.linear(5, 15)).map(Message(_)).log("message")
  } yield runIO {
    val greeting = Greeting[IO](message)
    val expected = Message(s"${message.value} ${name.value}")

    println(s"got $name and $message")
    greeting.get(name).map { actual =>
      actual ==== expected
    }
  }

  def add(a: Int, b: Int): Int = a + b

  def testGreet2: Property = for {
    a <- Gen.int(Range.linear(0, Int.MaxValue)).log("a")
    b <- Gen.int(Range.linear(0, Int.MaxValue)).log("b")
  } yield {

    println(s"adding $a $b = ${a + b}")

    Result.all(
      List(
        add(a, b) ==== add(b, a),
        add(a, 0) ==== a
      )
    )

  }

  def testSomething: Result = {
    val expected = 0
    val actual   = 0
    (actual ==== expected).log("the actual value is not 0")
  }
}
