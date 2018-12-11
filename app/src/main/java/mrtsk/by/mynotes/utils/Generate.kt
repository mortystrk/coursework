package mrtsk.by.mynotes.utils

import java.math.BigInteger
import java.security.SecureRandom

fun random(n: Int): String {
    val secureRandom = SecureRandom()
    return BigInteger(16 * n, secureRandom).toString(32).substring(n)
}