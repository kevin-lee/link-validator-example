package linkvalidator.core.parser

import cats.effect.*
import laika.api.Transformer as LaikaTransformer

/** @author Kevin Lee
  * @since 2025-08-26
  */
trait Transformer[F[*]] {
  def fromMarkdownToHtml(markdown: String): F[String]
}
object Transformer {
  def apply[F[*]: Sync](transformer: LaikaTransformer): Transformer[F] = new TransformerF(transformer)

  final private class TransformerF[F[*]: Sync as sync](transformer: LaikaTransformer) extends Transformer[F] {
    override def fromMarkdownToHtml(markdown: String): F[String] =
      sync.delay(
        transformer.transform(markdown).getOrElse("") // You need proper error handling here
      )
  }
}
