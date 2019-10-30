import domain.{AuditService, MailAddress, MyCoolAppContext, PlainPassword, User, UserRepository}

package object usecase {

  trait AuthenticationError
  object VerificationFailed extends AuthenticationError

  class LogInConsole[Context <: MyCoolAppContext](repository: UserRepository[Context], service: AuditService[Context]) {
    def run(email: MailAddress, password: PlainPassword)(implicit ctx: Context): ctx.Result[AuthenticationError, User] =
      for {
        user <- repository.resolveByEmail(email)
        isValidPass = user.hashedPassword.verify(password)
        _ <- ctx.unless(isValidPass)(VerificationFailed)
        _ <- service.recordLogin(user)
      } yield user
  }

}
