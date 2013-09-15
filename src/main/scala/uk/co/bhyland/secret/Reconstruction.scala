package uk.co.bhyland.secret

sealed trait ReconstructionResult
case class Success(secret: Array[Int]) extends ReconstructionResult
case object Error extends ReconstructionResult

object Reconstruction {
  
  def reconstruct(shares: List[Array[Int]]): ReconstructionResult = {
    val shareLengths = shares.map(_.length).removeDuplicates
    val shareIndexes = shares.map(s => s(0)).removeDuplicates
    
    if(shareLengths.length != 1) Error
    else if(shareIndexes.length != shares.length) Error
    else {
      
      val M = shares.length
      val L = shareLengths(0) - 1
      
      val secret = Array.fill[Int](L)(0)
      val U = Array.tabulate[Int](M)(i => shares(i)(0))
            
      for(j <- (1 to L)) {
        val V = Array.tabulate[Int](M)(i => shares(i)(j))
        secret(j - 1) = I(M, U, V)
      }
        
      Success(secret)
    }
  }
  
  import GF256._
  
  private def L(i: Int, U: Array[Int]) = {
    val M = U.length
    
    require(i >= 0)
    require(i < M)
    
    gf_product(
      (0 until M).filter(_ != i).map { j =>
        U(j) |/| (U(j) |+| U(i))
      }.toArray
    )
  }
  
  private def I(M: Int, U: Array[Int], V: Array[Int]) = {
    require(U.length == M)
    require(V.length == M)
    
    gf_sum(
      (0 until M).map { i =>
        L(i, U) |*| V(i)
      }.toArray
    )
  }
}
