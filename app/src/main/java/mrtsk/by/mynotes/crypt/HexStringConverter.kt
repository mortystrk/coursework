package mrtsk.by.mynotes.crypt

import java.io.UnsupportedEncodingException
import kotlin.experimental.and

class HexStringConverter internal constructor() {
    @Throws(UnsupportedEncodingException::class)
    fun stringToHex(input:String):String {
        if (input == null) throw NullPointerException()
        return asHex(input.toByteArray())
    }
    fun hexToString(txtInHex:String):String {
        val txtInByte = ByteArray(txtInHex.length / 2)
        var j = 0
        var i = 0
        while (i < txtInHex.length)
        {
            txtInByte[j++] = java.lang.Byte.parseByte(txtInHex.substring(i, i + 2), 16)
            i += 2
        }
        return String(txtInByte)
    }
    private fun asHex(bytes:ByteArray):String {
        val ss = String.format("%032x", bytes)
        return ss
    }

    companion object {
        private val HEX_CHARS = "0123456789abcdef".toCharArray()
        private var hexStringConverter: HexStringConverter? = null
        val hexStringConverterInstance:HexStringConverter
            get() {
                if (hexStringConverter == null) hexStringConverter = HexStringConverter()
                return hexStringConverter as HexStringConverter
            }
    }
}