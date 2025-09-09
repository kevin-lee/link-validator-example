package linkvalidator.app

import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import extras.render.syntax.*
import linkvalidator.core.{Greeter, Greeting}
import linkvalidator.core.types.{Message, Name}

trait GreetingApp[F[*]] {
  def run(message: Message, name: Name): F[Unit]
}

object GreetingApp {
  def apply[F[*]: {Sync, Console}]: GreetingApp[F] = new GreetingAppF

  private class GreetingAppF[F[*]: {Sync as F, Console as C}] extends GreetingApp[F] {
// OR
//  private class GreetingAppF[F[*]](using F: Sync[F], C: Console[F]) extends GreetingApp[F] {
    override def run(message: Message, name: Name): F[Unit] = for {
      _          <- F.delay(println(render"Hello! $name"))
      _          <- C.println("Please enter your name: ")
      nameString <- C.readLine
      _          <- F.delay(println("-----------------------"))

      name1 <- F.pure(Name(nameString))
      name2 <- F.pure(name)
      greeting = Greeting[F](message)
      greeter <- Sync[F].pure(Greeter[F](greeting))
      _       <- greeter.greet(name1)
      _       <- greeter.greet(name2)
    } yield ()
  }

}
