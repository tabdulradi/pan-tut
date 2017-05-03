## Sorting in Java

This is how we sort an `ArrayList` of `String`s in Java
```tut:book
import java.util._
val l = new ArrayList[String]()
l.add("Foo"); l.add("Bar"); l.add("Baz")
Collections.sort(l)
println(l)
```
This works for `String`s, `Int`s, and other built-in types because they implement `java.lang.Comparable`. So if we have custom classes that we like them to be sortable as well, we'll need to implement the interface too.
```tut:book
case class Employee(name: String) extends Comparable[Employee] {
  override def compareTo(o: Employee): Int = this.name.compareTo(o.name)
}
val l2 = new ArrayList[Employee]
l2.add(Employee("Foo")); l2.add(Employee("Bar")); l2.add(Employee("Baz"))

Collections.sort(l2)
println(l2)
```

The solution above might not be suitable if we don't own the class Employee (i.e it is defined in some other 3rd part lib). Fortunately, there is an alternative solution in Java land.
```tut:book
case class Employee(name: String)

class EmployeeComparator extends Comparator[Employee] {
  override def compare(o1: Employee, o2: Employee): Int =
    o1.name.compareTo(o2.name)
}

val l3 = new ArrayList[Employee]; l3.add(Employee("Foo")); l3.add(Employee("Bar")); l3.add(Employee("Baz"))

Collections.sort(l3, new EmployeeComparator)
println(l3)
```
We've just extracted the comparing functionality into a separate class whose only purpose it to sort our class.
This pattern is more or less a type-class! Although our example above is very verbose, we now try to rewrite it in an idiomatic Scala syntax.
