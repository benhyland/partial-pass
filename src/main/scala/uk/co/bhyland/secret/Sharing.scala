package uk.co.bhyland.secret

import scala.util.Random

trait Randomness {
  def nextOctet: Int
}

class Sharing(r: Randomness) {
  
  def share(secret: Array[Int], totalShares: Int, sharesRequiredForReconstruction: Int) = {
    val S = secret
    val L = secret.length
    val M = sharesRequiredForReconstruction
    val N = totalShares
    
    require(L <= 65534)
    require(1 <= M)
    require(M <= N)
    require(N <= 255)
    
    calculateShares(S, L, M, N)
  }
  
  private def calculateShares(S: Array[Int], L: Int, M: Int, N: Int) = {
    val shares = initialiseShares(L, N)

    for(i <- 0 until L) {
      val A = Array.fill[Int](M)(r.nextOctet)
      A(0) = S(i)
      for(share <- shares) {
        share(i + 1) = f(share(0), A)
      }
    }
    
    shares.toList
  }
  
  private def initialiseShares(L: Int, N: Int) = {
    val shareIndexes = (1 to N)
    shareIndexes.map { i =>
      val share = Array.fill[Int](L + 1)(0)
      share(0) = i
      share
    }
  }
  
  import GF256._
  private def f(X: Int, A: Array[Int]) = {
    gf_sum(A.zipWithIndex.map { case (a,i) => a |*| (X |^| i) })
  }
}

object Sharing {
  val random = new Randomness {
    val r = new Random()
    override def nextOctet = r.nextInt & 0xFF
  }
  def apply() = new Sharing(random)
}