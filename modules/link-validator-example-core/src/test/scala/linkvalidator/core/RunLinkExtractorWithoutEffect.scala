package linkvalidator.core

import laika.api.Transformer as LaikaTransformer
import laika.format.{HTML, Markdown}
import linkvalidator.core.extracter.LinkExtractorWithoutEffect
import linkvalidator.core.parser.TransformerWithoutEffect

/** @author Kevin Lee
  * @since 2025-08-26
  */
object RunLinkExtractorWithoutEffect {

  @main
  def run(): Unit = {

//    val markdownToHtmlTransformer = LaikaTransformer.from(ReStructuredText).to(HTML).withRawContent.build
    val markdownToHtmlTransformer = LaikaTransformer.from(Markdown).to(HTML).withRawContent.build

    val transformer = TransformerWithoutEffect(markdownToHtmlTransformer)
    val extractor   = LinkExtractorWithoutEffect()

    val input =
      """# Heading 1
        |## Heading 2
        |* Blah blah
        |* [Here](http://localhost:8080)
        |* [Google](https://www.google.com)
        |* <a href="https://www.gmail.com">Gmail</a>
        |* <a href="https://www.google.com">Google</a>
        |""".stripMargin

    val linkExtractorApp = LinkExtractorAppWithoutEffect(extractor, transformer)

    val links = linkExtractorApp.extract(input)

    println(
      s"""Links found:
         |${links.mkString("  - ", "\n  - ", "")}
         |""".stripMargin
    )
  }

  class LinkExtractorAppWithoutEffect(
    linkExtractor: LinkExtractorWithoutEffect,
    transformer: TransformerWithoutEffect
  ) {
    def extract(input: String): List[String] = {
      val html  = transformer.fromMarkdownToHtml(input)
      val links = linkExtractor.extract(html)
      links
    }
  }
}
