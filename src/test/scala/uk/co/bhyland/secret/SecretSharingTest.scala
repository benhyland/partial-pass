package uk.co.bhyland.secret

import scala.util.Random

import org.scalacheck.Gen
import org.scalacheck.Shrink
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class SecretSharingTest extends FunSuite with ShouldMatchers with GeneratorDrivenPropertyChecks {
  
  val uByteGen = Gen.choose(0, 255)
  val arrGen = Gen.containerOfN[Array, Int](3, uByteGen)
  implicit val noShrink = Shrink[Int](i => Stream.empty)
  implicit val noShrinkArr = Shrink[Array[Int]](i => Stream.empty)

  test("round trip of sharing and reconstruction") {
    val sharing = Sharing()
    forAll(arrGen) { (secret: Array[Int]) =>
      
      val n = 4      
      val shares = sharing.share(secret, n, n/2)            
      val candidateShares = Random.shuffle(shares).take(n/2)

      val result = Reconstruction.reconstruct(candidateShares)
      
      result.getClass() should be (classOf[Success])
      result.asInstanceOf[Success].secret should be (secret)
    }
  }
  
  test("known answer test for reconstruction") {
    val secret = Array(0x74, 0x65, 0x73, 0x74, 0x00)
    val M = 2
    val N = 2
    val shares = List(
        Array(0x01, 0xB9, 0xFA, 0x07, 0xE1, 0x85),
        Array(0x02, 0xF5, 0x40, 0x9B, 0x45, 0x11)
        )
    
    val result = Reconstruction.reconstruct(shares)
    
    result.getClass() should be (classOf[Success])
    result.asInstanceOf[Success].secret should be (secret)
  }
}