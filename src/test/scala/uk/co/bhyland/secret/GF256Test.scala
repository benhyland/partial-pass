package uk.co.bhyland.secret

import org.scalacheck.Gen
import org.scalacheck.Shrink
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import GF256.IntGF256
import GF256.gf_product
import GF256.gf_sum

class GF256Test extends FunSuite with ShouldMatchers with GeneratorDrivenPropertyChecks {
  
  val uByteGen = Gen.choose(0, 255)
  implicit val noShrink = Shrink[Int](i => Stream.empty)
  def forAllA = forAll(uByteGen) _
  def forAllAB = forAll(uByteGen, uByteGen) _
  def forAllABC = forAll(uByteGen, uByteGen, uByteGen) _
    
  test("addition is associative") {
    forAllABC { (a: Int, b: Int, c: Int) =>
      val ab_c = (a |+| b) |+| c
      val a_bc = a |+| (b |+| c)
      ab_c should be (a_bc)
    }
  }
  
  test("addition is its own inverse") {
    forAllAB { (a: Int, b: Int) =>
      val abb = (a |+| b) |+| b
      abb should be (a)
    }
  }
  
  test("addition is commutative") {
    forAllAB { (a: Int, b: Int) =>
      val ab = a |+| b
      val ba = b |+| a
      ab should be (ba)
    }
  }
  
  test("addition is equivalent to subtraction") {
    forAllAB { (a: Int, b: Int) =>
      a |+| b should be (a |-| b)
    }
  }
  
  test("identity for addition") {
    forAllA { (a: Int) =>
      a |+| 0 should be (a)
    }
  }
  
  test("multiplication is associative") {
    forAllABC { (a: Int, b: Int, c: Int) =>
      val ab_c = (a |*| b) |*| c
      val a_bc = a |*| (b |*| c)
      ab_c should be (a_bc)
    }
  }
  
  test("multiplication is commutative") {
    forAllAB { (a: Int, b: Int) =>
      val ab = a |*| b
      val ba = b |*| a
      ab should be (ba)
    }
  }
  
  test("zero for multiplication") {
    forAllA { (a: Int) =>
      a |*| 0 should be (0)
    }
  }
  
  test("identity for multiplication") {
    forAllA { (a: Int) =>
      a |*| 1 should be (a)
    }
  }
  
  test("identity for division") {
    forAllA { (a: Int) =>
      a |/| 1 should be (a)
    }
  }
  
  test("division is inverse for multiplication") {
    forAllAB { (a: Int, b: Int) =>
      whenever(b != 0) {
        val ab = (a |*| b)
        ab |/| b should be (a)
      }
    }
  }
  
  test("multiplication distributes over addition") {
    forAllABC { (a: Int, b: Int, c: Int) =>
      val a_bc = a |*| (b |+| c)
      val ab_ac = (a |*| b) |+| (a |*| c)
      a_bc should be (ab_ac)
    }
  }
  
  test("power of n multiplies n times") {
    forAllAB { (a: Int, b: Int) =>
      whenever(b != 0) {
        var apowb = 1
        var times = b
        while(times > 0) {
          times = times - 1
          apowb = apowb |*| a
        }
        a |^| b should be (apowb)
      }
    }
  }
  
  test("sum adds its inputs and produces the right field element") {
    forAllABC { (a: Int, b: Int, c: Int) =>
      val abc = (a ^ b ^ c) % 256
      gf_sum(Array(a, b, c)) should be (abc)
    }
  }
  
  test("product multiplies its inputs and produces the right field element") {
    forAllABC { (a: Int, b: Int, c: Int) =>
      val abc = (a |*| a |*| a |*| b |*| b |*| b |*| c |*| c |*| c)
      gf_product(Array(a, b, c, a, b, c, a, b, c)) should be (abc)
    }
  }
}
