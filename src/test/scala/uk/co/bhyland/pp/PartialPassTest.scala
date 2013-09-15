package uk.co.bhyland.pp

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import uk.co.bhyland.secret.Sharing

class PartialPassTest extends FunSuite with ShouldMatchers {

  val pp = new PartialPass(Sharing.random, 12, 3, 10)
  
  val password = "helloworld"
    
  test("simple partial password example") {
    
    val shares = pp.share(password)
    
    val candidate = CandidateShares(
      shares.secret,
      List(shares.shares(2), shares.shares(4), shares.shares(7)),
      "lor"
    )
    
    val ok = pp.authenticate(candidate)
    
    ok should be (true)
  }
}