import domain.{MyCoolAppContext, MyCoolAppResult}

import scala.concurrent.{ExecutionContext, Future}

package object infra {

  type DBSession

  class MyCoolAppContextImpl(ec: ExecutionContext, dbSession: DBSession) extends MyCoolAppContext {
    override type Result[+E, +A] = MyCoolAppResultImpl[E, A]

    private implicit val _ec: ExecutionContext = ec

    case class MyCoolAppResultImpl[+E, +A](value: Future[Either[E, A]]) extends MyCoolAppResult[MyCoolAppResultImpl, E, A] {

      override def map[B](f: A => B): MyCoolAppResultImpl[E, B] =
        MyCoolAppResultImpl(value.map(_.map(f)))

      override def flatMap[B, EE >: E](f: A => MyCoolAppResultImpl[EE, B]): MyCoolAppResultImpl[EE, B] =
        MyCoolAppResultImpl {
          value.flatMap {
            case Left(e) => Future.successful(Left(e))
            case Right(a) => f(a).value
          }
        }
    }

    override def success[A](a: A): Result[Nothing, A] =
      MyCoolAppResultImpl(Future.successful(Right(a)))

    override def fail[E](e: E): Result[E, Nothing] =
      MyCoolAppResultImpl(Future.successful(Left(e)))
  }

}
