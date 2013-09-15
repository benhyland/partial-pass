package uk.co.bhyland.pp

import java.nio.charset.Charset

import scala.Array.canBuildFrom

import uk.co.bhyland.secret.GF256.IntGF256
import uk.co.bhyland.secret.Randomness
import uk.co.bhyland.secret.Reconstruction
import uk.co.bhyland.secret.Sharing
import uk.co.bhyland.secret.Success

case class Shares(secret: Array[Int], shares: List[Array[Int]])
case class CandidateShares(secret: Array[Int], shares: List[Array[Int]], partialPassword: String)

// TODO:
// better error reporting and negative tests
// better api, particularly for pp input construction and reconstruction results

class PartialPass(
    random: Randomness,
    l: Int, // length of secret to generate (each share has length l + 1)
    k: Int, // number of shares required to authenticate (i.e. number of characters required)
    n: Int  // total number of shares (i.e. length of password)
    ) {
  
  require(k <= n)
  require(k > 0)
  
  private val sharing = new Sharing(random)
  
  def share(password: String) = {  

    val secret = generateSecret(l)
    
    val characters = getCharacters(password)
  
    require(characters.length == password.length, "Sorry, can't handle passwords with multi-byte characters")
    require(characters.length == n, "Sorry, partial passwords must have length " + n)
    
    val shares = sharing.share(secret, characters.length, k)

    val sharesWithCharacter = characteriseShares(shares, characters)
    
    Shares(secret, sharesWithCharacter)
  }
  
  def authenticate(candidateShares: CandidateShares) = {
    
    require(candidateShares.shares.length == k, "Sorry, candidate must supply " + k + " stored shares")
    require(candidateShares.partialPassword.length == k, "Sorry, candidate must supply partial password of length " + k)

    val shares = candidateShares.shares
    val characters = getCharacters(candidateShares.partialPassword)
    
    require(characters.length == candidateShares.partialPassword.length, "Sorry, can't handle passwords with multi-byte characters")

    val sharesWithoutCharacter = characteriseShares(shares, characters)

    val result = Reconstruction.reconstruct(sharesWithoutCharacter)
    result match {
      case Success(secret) => secret.sameElements(candidateShares.secret)
      case _ => false
    }
  }
  
  private def generateSecret(length: Int) = Array.fill[Int](length)(random.nextOctet)
  
  private def getCharacters(password: String) = password.getBytes(Charset.forName("UTF-8")).map(_ & 0xFF)
  
  // note GF256.|+| is its own inverse
  private def characteriseShares(shares: List[Array[Int]], characters: Array[Int]) =
    shares.zip(characters).map { case (share, c) =>
      share.map(_ |+| c)
    }
}