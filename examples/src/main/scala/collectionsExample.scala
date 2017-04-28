// Example: Scala collections
//
// Scala provides some nice collections.
//
// _(Almost copied from [https://twitter.github.io/scala_school/collections.html](https://twitter.github.io/scala_school/collections.html))_

// @hide
object Collections {
// ## Arrays
// Arrays preserve order, can contain duplicates, and are mutable.
Array(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
// ## Lists
// Lists preserve order, can contain duplicates, and are immutable.
List(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
// ## Sets
// Sets do not preserve order and have no duplicates.
Set(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
// ## Tuple
// A tuple groups together simple logical collections of items without using a class.
val hostPort = ("localhost", 80)
// Unlike case classes, they don’t have named accessors, instead they have accessors that 
// are named by their position and is 1-based rather than 0-based.
val host = hostPort._1
val port = hostPort._2
// Tuples fit with pattern matching nicely.
hostPort match {
  case ("localhost", port) => ???
  case (host, port) => ???
}
// Tuple has some special sauce for simply making Tuples of 2 values: `->`
1 -> 2
// ## Maps
// It can hold basic datatypes.
Map(1 -> 2)
Map("foo" -> "bar")
// This looks like special syntax but remember back to our discussion of Tuple 
// that `->` can be use to create _Tuples_.
//
// Map() also uses that variable argument syntax we learned befre: `Map(1 -> "one", 2 -> "two")` 
// which expands into `Map((1, "one"), (2, "two"))` with the first element being the key and the 
// second being the value of the Map.
//
// Maps can themselves contain Maps or even functions as values.
Map(1 -> Map("foo" -> "bar"))

def timesTwo(a: Int): Int = a * 2
Map("timesTwo" -> { timesTwo(_) })
// ## Option
// Option is a container that may or may not hold something.
//
// `Map.get` uses Option for its return type. Option tells you that the method might not return 
// what you’re asking for.
val numbers = Map("one" -> 1, "two" -> 2)
assert(numbers.get("two") == Some(2))
assert(numbers.get("three") == None)
// Now our data appears trapped in this Option. How do we work with it?
// 
// A first instinct might be to do something conditionally based on the isDefined method.
// We want to multiply the number by two, otherwise return 0.
def maybeTimesTwo(a: Option[Int]): Int = {
  if (a.isDefined) {
    a.get * 2
  } else {
    0
  }
}
// We would suggest that you use either `getOrElse` or pattern matching to work with this result.
//
// `getOrElse` lets you easily define a default value.
val maybeInt: Option[Int] = ???
val result = maybeInt.getOrElse(0) * 2
// Pattern matching fits naturally with Option.
maybeInt match {
  case Some(n) => n * 2
  case None => 0
}
// @hide
}