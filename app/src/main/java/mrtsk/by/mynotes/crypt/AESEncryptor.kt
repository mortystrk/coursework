package mrtsk.by.mynotes.crypt

import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.BlockCipherPadding
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.jce.ECNamedCurveTable
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import javax.xml.bind.DatatypeConverter

val ECParams = ECNamedCurveTable.getParameterSpec("secp256k1")

class AESEncryptor {
    private val engine = CBCBlockCipher(AESEngine())
    private lateinit var random: SecureRandom

    private val bcp: BlockCipherPadding = PKCS7Padding()
    private lateinit var key: KeyParameter


    fun init(IV: ByteArray) {
        random = SecureRandom(IV)
        key = KeyParameter(IV.copyOfRange(0, 32))
    }

    fun encrypt(input: String): String {
        return toHexString(processing(input.toByteArray(), true))
    }

    fun decrypt(input: String): String {
        return String(processing(toByteArray(input), false))
    }

    private fun processing(input: ByteArray, encrypt: Boolean): ByteArray {

        val pbbc = PaddedBufferedBlockCipher(engine, bcp)

        val blockSize = engine.blockSize
        var inputOffset = 0
        var inputLength = input.size
        var outputOffset = 0

        val iv = ByteArray(blockSize)
        if (encrypt) {
            random.nextBytes(iv)
            outputOffset += blockSize
        } else {
            System.arraycopy(input, 0, iv, 0, blockSize)
            inputOffset += blockSize
            inputLength -= blockSize
        }

        pbbc.init(encrypt, ParametersWithIV(key, iv))
        val output = ByteArray(pbbc.getOutputSize(inputLength) + outputOffset)

        if (encrypt) {
            System.arraycopy(iv, 0, output, 0, blockSize)
        }

        var outputLength = outputOffset + pbbc.processBytes(
            input, inputOffset, inputLength, output, outputOffset)

        outputLength += pbbc.doFinal(output, outputLength)

        return Arrays.copyOf(output, outputLength)

    }

    private fun toHexString(array: ByteArray): String {
        return DatatypeConverter.printHexBinary(array)
    }

    private fun toByteArray(s: String): ByteArray {
        return DatatypeConverter.parseHexBinary(s)
    }
}