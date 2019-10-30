
object Main extends App {

  trait Foo {
    type Result
    def value: Result
  }

  def bar(foo: Foo): foo.Result = foo.value

  object StringFoo extends Foo {
    override type Result = String
    override def value: String = "piyopiyo"
  }

  object IntFoo extends Foo {
    override type Result = Int
    override def value: Int = 10
  }

  println(bar(StringFoo))
  println(bar(IntFoo))
}
