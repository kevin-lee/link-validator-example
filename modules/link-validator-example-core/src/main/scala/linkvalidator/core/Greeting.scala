package linkvalidator.core

import cats.effect.Sync
import extras.render.syntax.*
import types.{Message, Name}

trait Greeting[F[*]] {
  def get(name: Name): F[Message]
}
object Greeting {
  def apply[F[*]: Sync](message: Message): Greeting[F] = new GreetingF[F](message)

  private class GreetingF[F[*]: Sync](message: Message) extends Greeting[F] {
    override def get(name: Name): F[Message] = Sync[F].pure(Message(render"$message $name"))
  }
}
