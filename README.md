Partial-Pass
============

A basic Scala implementation of [partial passwords](http://en.wikipedia.org/wiki/Partial_Password) relying on ideas from this [threshold secret sharing](http://tools.ietf.org/html/draft-mcgrew-tss-03) expired draft proposal.
TSS is a refinement of [Shamir's secret sharing](http://en.wikipedia.org/wiki/Shamir%27s_Secret_Sharing) algorithm which works by considering the secret and shares on the level of individual bytes.
It has some restrictions on secret size and share counts which are unlikely to matter in practical applications.
The IETF draft may be helpful when deciphering some of the more idiosyncratic names in the code.

Note that while this method allows partial passwords to be used securely without unreasonable space requirements, any security system using it will still be as vulnerable to mitm, phishing or social engineering as simpler methods.
Know your threat model!
In particular, don't bother with this if you are likely to be subject to recording attacks.
See ["Give me Letters 2, 3 and 6! ": Partial Password Implementation & Attacks](http://groups.inf.ed.ac.uk/security/passwords), David Aspinall and Mike Just.
Also [read this](http://www.securityfocus.com/blogs/2009) and be warned.

I wrote this as an exercise in [ScalaCheck](http://www.scalatest.org/user_guide/writing_scalacheck_style_properties) and because last time I looked I couldn't find any implementation of partial passwords in Java or Scala.

See also this somewhat neater and more comprehensive [python implementation](https://github.com/seb-m/tss) of TSS.