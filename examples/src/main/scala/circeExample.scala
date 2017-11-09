// Example: Parsing JSON with circe.
//
// Circe is a JSON library for Scala powered by [Cats](http://typelevel.org/cats/).
// Let's use it to parse a JSON fragment and extract some data.
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._

object ParseSomeJson {

  // Here we declare a simple JSON string.
  val json = """
    {
      "id": "c730433b-082c-4984-9d66-855c243266f0",
      "values": {
        "bar": true,
        "baz": 100.001
      }
    }
  """

  // Let's we parse the JSON string into a `Json` value.
  // We need provide a default in case of the JSON cannot be parsed.
  val doc: Json = parse(json).getOrElse(Json.Null)

  for {
    // We can now use [Optics](https://circe.github.io/circe/optics.html) to traverse 
    // the `Json` value and extract the relevant parts.
    bar <- root.values.bar.boolean.getOption(doc)
    baz <- root.values.baz.double.getOption(doc)
  } yield (bar,baz)

}