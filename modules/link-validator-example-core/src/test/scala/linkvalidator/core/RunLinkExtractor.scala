package linkvalidator.core

import cats.effect.*
import cats.syntax.all.*
import laika.api.Transformer as LaikaTransformer
import laika.format.{HTML, Markdown}
import linkvalidator.core.extracter.LinkExtractor
import linkvalidator.core.parser.Transformer

/** @author Kevin Lee
  * @since 2025-08-26
  */
object RunLinkExtractor extends IOApp.Simple {

  def run: IO[Unit] = {

//    val markdownToHtmlTransformer = LaikaTransformer.from(ReStructuredText).to(HTML).withRawContent.build
    val markdownToHtmlTransformer = LaikaTransformer.from(Markdown).to(HTML).withRawContent.build

    val transformer = Transformer[IO](markdownToHtmlTransformer)
    val extractor   = LinkExtractor[IO]()

    val input =
      """# Heading 1
        |## Heading 2
        |* Blah blah
        |* [Here](http://localhost:8080)
        |* [Google](https://www.google.com)
        |* <a href="https://www.gmail.com">Gmail</a>
        |* <a href="https://www.google.com">Google</a>
        |""".stripMargin

    val linkExtractorApp = LinkExtractorApp[IO](extractor, transformer)

    for {
      links <- linkExtractorApp.extract(input)
      _     <- IO.println(
                 s"""Links found:
                |${links.mkString("  - ", "\n  - ", "")}
                |""".stripMargin
               )
    } yield ()
  }

  private class LinkExtractorApp[F[*]: Sync](linkExtractor: LinkExtractor[F], transformer: Transformer[F]) {
    def extract(input: String): F[List[String]] = for {
      html  <- transformer.fromMarkdownToHtml(input)
      links <- linkExtractor.extract(html)
    } yield links
  }
}
