## Idiomatic Scala syntax
Let's first build our own version of `Comparator` and `sort`, then try to make them more idiomatic step-by-step
```tut:silent
trait Comparator[T] {
  def compare(o1: T, o2: T): Int
}

object Collections {
  // Inefficient quick sort implementation
  def sort[T](xs: Seq[T], comparator: Comparator[T]): Seq[T] =  xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st, comparator) ++ Seq(head) ++ sort(gte, comparator)
  }
}
```

Now let's define our Employee comparator
```tut:invisible
final case class Employee(name: String)
```
```tut:book
object EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}
```
Notice that `EmployeeComparator` is a singleton object rather than a class, since we don't wish to parameterise it.

```tut:book
val l = Seq[Employee](Employee("Foo"), Employee("Bar"), Employee("Baz"))
Collections.sort(l, EmployeeComparator)
```

### Using Implicits
```tut:book
object Collections {
  def sort[T](xs: Seq[T])(implicit comparator: Comparator[T]): Seq[T] = xs match {
    case Seq() => Seq.empty
    case head +: tail =>
      val (st, gte) = tail.partition(comparator.compare(xs.head, _) > 0)
      sort(st) ++ Seq(head) ++ sort(gte)
  }
}
```
In the example above, we defined `comparator` in a separate parameter list, that is marked with `implicit` modifier.
This modifier instructs Scala to look if for a suitable value (of the right type) to be passed in the method call. As you see in line 6, we didn't have to specify the parameter `comparator` ourselves, it has been implicitly specified for us!

Likewise, by marking our Comparator instances as implicit, we'll be able to call `sort` without specifying the comparator explicitly.
```tut:book
implicit object EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}

Collections.sort(l)
```

## Recursive implicits
Scala's implicits search allows implicit definitions to depend on each other.
For example, let's build on top a Comparator for Option[Employee].
```scala
implicit val optionOfEmployeeComparator = new Comparator[Option[Employee]] {
  override def compare(o1: Option[Employee], o2: Option[Employee]): Int =
    (o1, o2) match {
      case (None, None) => 0
      case (None, Some(_)) => -1
      case (Some(_), None) => 1
      case (Some(e1), Some(e2)) => EmployeeComparator.compare(e1, e2)
    }
}
```
The example above works only for `Option[Employee]`, but the implementation itself can work for Option of any type (as longs as this type has a comparator instance). Luckily we have a way to do this:

```tut:book
implicit def optionComparator[T](implicit innerComparator: Comparator[T]) = new Comparator[Option[T]] {
  override def compare(o1: Option[T], o2: Option[T]): Int =
    (o1, o2) match {
      case (None, None) => 0
      case (None, Some(_)) => -1
      case (Some(_), None) => 1
      case (Some(e1), Some(e2)) => innerComparator.compare(e1, e2)
    }
}

Collections.sort(Seq(Some(Employee("Foo")), None, Some(Employee("Bar")), Some(Employee("Baz"))))
```
The compiler will search for a suitable instance of the type-class, or at least, a way to derive it.
So it finds a way to derive `Comparator[Option[T]]` (optionComparator), but it depends on `Comparator[T]`. Luckily we have `EmployeeComparator` which fulfils the requirement.  

If we tried with `Option[Int]` for example, the code won't compile
```tut:book:fail
Collections.sort(Seq(Some(1), None, Some(3), Some(2)))
```
Unless, we defined an instance for Ints!
```tut:book
implicit val intComparator = new Comparator[Int] {
  override def compare(o1: Int, o2: Int): Int = o1 - o2
}
```
```tut:book
Collections.sort(Seq(Some(1), None, Some(3), Some(2)))
```

## Wrapping up
We have seen how to implement Typeclass pattern elegantly using implicits.
In the next blog, we will introduce conventions evolved by community to define type-classes. Also we will introduce HLists, which makes type-classes even more powerful!


## Finally
I'll be talking about
