package linkvalidator.core

import hedgehog.{Gen, Range}

/** @author Kevin Lee
  * @since 2025-09-09
  */
object Gens {

  def genPerson: Gen[Person] = for {
    id   <- Gen.int(Range.linear(1, 100))
    name <- Gen.string(Gen.alpha, Range.linear(1, 100))
  } yield Person(id, name)

  def genHeading: Gen[String] = for {
    heading <- Gen.element1("#", "##", "###", "####")
    content <- Gen.string(Gen.unicode, Range.linear(1, 100))
  } yield s"$heading $content"

  def genMarkdownLink: Gen[(String, String)] = for {
    protocol <- Gen.element1("http", "https", "ftp", "mailto")
    title    <- Gen.string(Gen.unicode, Range.linear(1, 100))
    link     <- Gen.string(Gen.alphaNum, Range.linear(3, 20)).list(Range.linear(1, 4)).map(_.mkString("."))
  } yield (s"$protocol://$link}", s"[$title]($protocol://$link)")
}
final case class Person(id: Int, name: String)
