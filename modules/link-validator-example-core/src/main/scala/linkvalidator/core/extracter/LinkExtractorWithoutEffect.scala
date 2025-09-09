package linkvalidator.core.extracter

import org.jsoup.Jsoup

import scala.jdk.CollectionConverters.*

/** @author Kevin Lee
  * @since 2025-08-26
  */
trait LinkExtractorWithoutEffect {
  def extract(html: String): List[String]
}
object LinkExtractorWithoutEffect {
  def apply(): LinkExtractorWithoutEffect = new LinkExtractorWithoutEffect {
    override def extract(html: String): List[String] = {
      val doc = Jsoup.parse(html) // Each line may throw an exception.

      /* Extras anchor tags (e.g. <a href="https://www.google.com" target="_blank">Google</a>) */
      val selectedAs = doc.select("a[href]") // Each line may throw an exception.
      val hrefList = selectedAs.asList.asScala.map(_.attr("href")).distinct.toList // Each line may throw an exception.
      hrefList
    }
  }
}
