package linkvalidator.core.parser

import laika.api.Transformer as LaikaTransformer

/** @author Kevin Lee
  * @since 2025-08-26
  */
trait TransformerWithoutEffect {
  def fromMarkdownToHtml(markdown: String): String
}
object TransformerWithoutEffect {
  def apply(transformer: LaikaTransformer): TransformerWithoutEffect = new TransformerWithoutEffect {
    override def fromMarkdownToHtml(markdown: String): String =
      transformer.transform(markdown).getOrElse("")
  }
}
