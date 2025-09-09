package linkvalidator.core.extracter

import cats.effect.Sync
import cats.syntax.all.*
import org.jsoup.Jsoup

import scala.jdk.CollectionConverters.*

/** @author Kevin Lee
  * @since 2025-08-26
  */
trait LinkExtractor[F[*]] {
  def extract(html: String): F[List[String]]
}
object LinkExtractor {
  def apply[F[*]: Sync](): LinkExtractor[F] = new LinkExtractorF
  final private class LinkExtractorF[F[*]: Sync as sync] extends LinkExtractor[F] {
    override def extract(html: String): F[List[String]] = for {
      doc        <- sync.delay(Jsoup.parse(html))
      /* Extras anchor tags (e.g. <a href="https://www.google.com" target="_blank">Google</a>) */
      selectedAs <- sync.delay(doc.select("a[href]"))
      hrefList   <- sync.delay(selectedAs.asList.asScala.map(_.attr("href")).distinct.toList)
    } yield hrefList
  }
}
