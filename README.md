Partial-Pass
============

A basic implementation of [partial passwords](http://en.wikipedia.org/wiki/Partial_Password) using [Shamir's secret sharing](http://en.wikipedia.org/wiki/Shamir%27s_Secret_Sharing) algorithm.
Written in Scala, using [Spire](https://github.com/non/spire) for finite field arithmetic.

Note that while this method allows partial passwords to be used securely without unreasonable space requirements, it is still as vulnerable to mitm, phishing or social engineering as simpler methods. Know your threat model! In particular, don't bother with this if you are likely to be subject to recording attacks. See ["Give me Letters 2, 3 and 6! ": Partial Password Implementation & Attacks](http://groups.inf.ed.ac.uk/security/passwords), David Aspinall and Mike Just.
