package com.criteo

import scala.util._

object Compat {
  implicit class TryCompat[A](x: Try[A]) {
    def fold[U](fa: (Throwable) => U, fb: (A) => U): U =
      x match {
        case Success(v) => fb(v)
        case Failure(e) => fa(e)
      }
  }
  implicit class EitherCompat[A,B](x: Either[A,B]) {
    def getOrElse[B1 >: B](or: => B1): B1 =
      x match {
        case Left(_) => or
        case Right(v) => v
      }
  }
}