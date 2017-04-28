// Example: Statefully transforming streams with fs2.

// We often wish to statefully transform one or more streams in some way, possibly 
// evaluating effects as we do so. As an example, consider taking just the 
// first _n_ elements of a [`Stream`](fs2/Stream.html).
import fs2._

object TransformingStreams {

  // The core operation for implementing `take` is just a recursive function.
  def take[F[_],O](n: Int)(h: Handle[F,O]): Pull[F,O,Nothing] = {
    for {
      // If `n <= 0`, we're done, and stop pulling.
      // Otherwise we have more values to take, so we `h.awaitLimit(n)`.
      (chunk, h) <- if (n <= 0) Pull.done else h.awaitLimit(n)

      // `Pull.output(chunk)` writes the chunk we just read to the output of the _Pull_. 
      // The `p >> p2` operator is equivalent to `p flatMap { _ => p2 }`. It just runs 
      // `p` for its effects but ignores its result.
      tl <- Pull.output(chunk) >> take(n - chunk.size)(h)
    } yield tl
  }

  // Finally we simply use the `pull` method on Stream. It just calls open then close.
  //
  // _Note:_ The `.pure` converts a `Stream[Nothing,A]` to a `Stream[Pure,A]`. Scala will 
  // not infer `Nothing` for a type parameter, so using `Pure` as the effect provides 
  // better type inference in some cases.
  println {
    Stream(1,2,3,4).pure.pull(take(2)).toList
  }

}