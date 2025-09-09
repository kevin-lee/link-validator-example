package linkvalidator.core

import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all.*
import extras.render.*
import extras.render.syntax.*
import types.*

trait Greeter[F[*]] {
  def greet(name: Name): F[Unit]
}

object Greeter {
  def apply[F[*]: {Console, Sync}](greeting: Greeting[F]): Greeter[F] = new GreeterF[F](greeting)

  private class GreeterF[F[*]: {Console, Sync}](greeting: Greeting[F]) extends Greeter[F] {
    override def greet(name: Name): F[Unit] = for {
      message <- greeting.get(name)
      _       <- Console[F].println(render"$message!")
    } yield ()
  }
}
