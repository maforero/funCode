import org.scalatest.FunSuite

/**
  * Created by maria.forero on 23/10/2017.
  */
class PolymorphicLearningTests extends FunSuite{

  def isSorted[A](as: Array[A],ordered: (A,A) => Boolean): Any = {

      def loop(n: Int, response: Array[Boolean]): Boolean = {
        if(n >= as.length -1) response.foldLeft(true)(_&&_)
        else if(ordered(as(n + 1),as(n))) loop(n + 1 , response ++ Array(true))
        else loop(n + 1,response ++ Array(false))
      }
    loop(0,Array())
  }

  test("isSorted receives a sorted array and returns true"){
    assertResult(true)(isSorted(Array(1,2,3,4,5),(x: Int,y : Int) => x > y))
  }

  test("isSorted receives an unsorted array of ints and returns false"){
    assertResult(false)(isSorted(Array(2,1),(x: Int,y : Int) => x > y))
  }

  test("isSorted receives a sorted array of strings and returns true"){
    assertResult(true)(isSorted(Array("a","b"),(x: String,y :String) => x.compareTo(y) > 0 ))
  }

  test("isSorted receives an unsorted array of strings and returns false"){
    assertResult(false)(isSorted(Array("a","b","t","j"),(x: String,y :String) => x.compareTo(y) > 0 ))
  }

  test("isSorted receives a long sorted array of strings and returns true"){
    assertResult(true)(isSorted(Array("k","m","t","z"),(x: String,y :String) => x.compareTo(y) > 0 ))
  }
}
