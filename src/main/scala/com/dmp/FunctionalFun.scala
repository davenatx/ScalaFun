package com.dmp

/**
 * Excercises demonstrating functional concepts in Scala
 */
object FunctionalFun {

  /**
   * Partially Applied Function
   */
  def sum(x: Int, y: Int, z: Int) = x + y + z

  /* The main advantage to partially applied functions is you can
   * pass around defs as values.  
   */
  val a = sum _ // a: (Int, Int, Int) => <function3>

  a(1, 2, 3) // results in 6

  val b = sum _

  /* For example, I can pass b shown below as an argument to the
   * addThreeValues functoin that takes (Int, Int, Int) => Int.
   */
  addThreeValues(10, b)

  /* Sum x-1, x-2, and x-3 */
  def addThreeValues(x: Int, f: (Int, Int, Int) => Int) = {
    f(x - 1, x - 2, x - 3)
  }

  /* I can also partially apply some of the params */
  val c = sum(10, _: Int, 5)

  /**
   * Compose two functions
   */
  val toReverse = (s: String) => s.reverse

  val toUpper = (s: String) => s.toUpperCase

  val reverseAndUpper = toReverse compose toUpper

  reverseAndUpper("abcdefg")

  /**
   * Simple Currying
   *
   * Curried methods need the param list seperated in their own ().  When
   * this type of method is called, a function that takes the next param
   * is returned with the previous param already applied.  E.g. curriedSum
   * shown below is executed like this:
   *
   * def first(x: Int) = (y: Int) => x + y
   * first: (x: Int)(Int) => Int
   *
   * Applying 1 to the first function yields the second function:
   *
   * val second = first(1)
   * second: (Int) => Int = <functoin1>
   *
   * Applying 2 to the second function yields the result:
   *
   * second(2)
   * res0: Int = 3
   */
  def curriedSum(x: Int)(y: Int) = x + y

  val onePlus = curriedSum(1)_
  onePlus(2) // will result in 3

  def twice(op: Double => Double, x: Double) = op(op(x))
  twice(_ + 1, 5) //will result in 7.0

  /**
   * Loan Pattern
   */
  import java.io.File
  import java.io.PrintWriter

  def withPrintWriter(file: File)(op: PrintWriter => Unit) {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close
    }
  }

  val file = new File("date.txt")
  // Use currying to pass the higher-order function in {} so it looks more like 
  // a built in control structure
  withPrintWriter(file) { writer => writer.println(new java.util.Date) }

  /**
   * By-name parameters
   */
  var assertionsEnabled = true

  /* param is equivalent to (predicate: () => Boolean), but then it has
   * to be called like myAssert(() => 2 > 1).  This way it can be called with
   * simpy myAssert(2 > 1).  This shorthand is only valid for params, not vals,
   * etc...
   */
  def myAssert(predicate: => Boolean) = {
    if (assertionsEnabled && !predicate) throw new AssertionError
  }

  /* The difference between the By-name parameter version and the version below 
   * is that the version below evaluates the boolean expression when assertions
   * are disabled.  This method takes a boolean argument.  Conversly, the method 
   * above takes a higher-order function () => Boolean.  In this case, the function
   * is not evaluated unless assertions are enabled.
   */
  def boolAssert(predicate: Boolean) = {
    if (assertionsEnabled && !predicate) throw new AssertionError
  }

}

/**
 * Example of reducing code duplicatoin with higher-order functions
 *
 * This example queries filenames in the filesHere directory.  The
 * different query methods pass a higher-order fuction String => Boolean
 * to the filesMatching method.
 */
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles

  private def filesMatching(matcher: String => Boolean) = {
    for {
      file <- filesHere
      if matcher(file.getName)
    } yield file
  }

  def filesEnding(query: String) = filesMatching(_.endsWith(query))

  def filesContaining(query: String) = filesMatching(_.contains(query))

  def filesRegex(query: String) = filesMatching(_.matches(query))
}