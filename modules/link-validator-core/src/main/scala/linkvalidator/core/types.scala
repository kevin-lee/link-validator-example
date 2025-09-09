package linkvalidator.core

import refined4s.*
import refined4s.modules.cats.derivation.*
import refined4s.modules.extras.derivation.*

object types {

  type Name = Name.Type
  object Name extends Newtype[String], CatsEqShow[String], ExtrasRender[String]

  type Message = Message.Type
  object Message extends Newtype[String], CatsEqShow[String], ExtrasRender[String]

}
