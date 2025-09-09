package linkvalidator.core

import extras.scala.io.file.TempFiles
import hedgehog.*
import hedgehog.runner.*

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.Using

/** @author Kevin Lee
  * @since 2025-08-12
  */
object FileLoadSpec extends Properties {
  def tests: List[Test] = List(
    property("round-trip test for write and load", testLoad)
  )

  def testLoad: Property = for {
    content <-
      Gen
        .string(
          Gen.frequency1(
            80 -> Gen.alphaNum,
            20 -> Gen.element1('\t', ' ', '#', '%', '$')
          ),
          Range.linear(20, 100)
        )
        .list(Range.linear(1, 10))
        .map(_.mkString("\n"))
        .log("content")
  } yield {
    val expected = content
    TempFiles
      .runWithTempDir("my-temp-dir") { tempDir =>
        val rootDir  = tempDir.value
        val testFile = new File(rootDir, "test.txt")

        Using.resource(PrintWriter(testFile)) { writer =>
          writer.println(content)
        }

        Using(Source.fromFile(testFile)) { src =>
          val actual = src.getLines.mkString("\n")

          println(
            s""">>>> Loaded:
               |$actual
               |""".stripMargin
          )

          actual
        }.toEither

      }
      .joinRight match {
      case Right(actual) => actual ==== expected
      case Left(err) => Result.failure.log(s"Failed: ${err.getMessage}")
    }
  }

}
