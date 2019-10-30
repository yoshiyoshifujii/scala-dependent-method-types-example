package object domain {

  type MailAddress
  type PlainPassword
  trait HashedPassword {
    def verify(plainPassword: PlainPassword): Boolean
  }
  trait User {
    def hashedPassword: HashedPassword
  }
  type UserNotFound
  type Audit
  type RecordFailed

  trait MyCoolAppContext {
    type Result[+E, +A] <: MyCoolAppResult[Result, E, A]
    def success[A](a: A): Result[Nothing, A]
    def fail[E](e: E): Result[E, Nothing]

    def unless[E](bool: Boolean)(e: E): Result[E, Unit] =
      if (bool) success(()) else fail(e)
  }
  trait MyCoolAppResult[F[+_, +_], +E, +A] {
    def map[B](f: A => B): F[E, B]
    def flatMap[B, EE >: E](f: A => F[EE, B]): F[EE, B]
  }

  trait UserRepository[Context <: MyCoolAppContext] {
    def resolveByEmail(email: MailAddress)(implicit ctx: Context): ctx.Result[UserNotFound, User]
  }

  trait AuditService[Context <: MyCoolAppContext] {
    def recordLogin(user: User)(implicit ctx: Context): ctx.Result[RecordFailed, Audit]
  }

}
