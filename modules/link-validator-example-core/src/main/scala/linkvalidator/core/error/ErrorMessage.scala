package linkvalidator.core.error

import scala.util.control.NoStackTrace

final case class ErrorMessage(message: String) extends NoStackTrace {
  override val getMessage: String = message
}
