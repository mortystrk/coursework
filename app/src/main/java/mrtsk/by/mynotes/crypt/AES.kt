package mrtsk.by.mynotes.crypt

import java.io.BufferedReader
import java.io.FileWriter

class AES {

    enum class Mode {
        ECB, CBC
    }

    //Helper method which executes a deep copy of a 2D array. (dest,src)
    private fun deepCopy2DArray(destination: Array<IntArray>, source: Array<IntArray>) {
        assert(destination.size == source.size && destination[0].size == source[0].size)
        for (i in destination.indices) {
            System.arraycopy(source[i], 0, destination[i], 0, destination[0].size)
        }
    }

    /**
     * Pulls out the subkey from the key formed from the keySchedule method
     * @param km key formed from AES.keySchedule()
     * @param begin index of where to fetch the subkey
     * @return The chunk of the scheduled key based on begin.
     */

    private fun subKey(km: Array<IntArray>, begin: Int): Array<IntArray> {
        val arr = Array(4) { IntArray(4) }
        for (i in arr.indices) {
            for (j in arr.indices) {
                arr[i][j] = km[i][4 * begin + j]
            }
        }
        return arr
    }

    /**
     * Replaces all elements in the passed array with values in sbox[][].
     * @param arr Array whose value will be replaced
     * @return The array who's value was replaced.
     */
    fun subBytes(arr: Array<IntArray>) {
        for (i in arr.indices)
        //Sub-Byte subroutine
        {
            for (j in 0 until arr[0].size) {
                val hex = arr[j][i]
                arr[j][i] = sbox[hex / 16][hex % 16]
            }
        }
    }

    /**
     * Inverse rendition of the subBytes. The operations of invSubBytes are the reverse operations of subBytes.
     * @param arr the array that is passed.
     */

    fun invSubBytes(arr: Array<IntArray>) {
        for (i in arr.indices)
        //Inverse Sub-Byte subroutine
        {
            for (j in 0 until arr[0].size) {
                val hex = arr[j][i]
                arr[j][i] = invsbox[hex / 16][hex % 16]
            }
        }
    }

    /**
     * Performs a left shift on each row of the matrix.
     * Left shifts the nth row n-1 times.
     * @param arr the reference of the array to perform the rotations.
     */

    fun shiftRows(arr: Array<IntArray>) {
        for (i in 1 until arr.size) {
            arr[i] = leftrotate(arr[i], i)
        }
    }

    /**
     * Left rotates a given array. The size of the array is assumed to be 4.
     * If the number of times to rotate the array is divisible by 4, return the array
     * as it is.
     * @param arr The passed array (assumed to be of size 4)
     * @param times The number of times to rotate the array.
     * @return the rotated array.
     */

    private fun leftrotate(arr: IntArray, times: Int): IntArray {
        var times = times
        assert(arr.size == 4)
        if (times % 4 == 0) {
            return arr
        }
        while (times > 0) {
            val temp = arr[0]
            for (i in 0 until arr.size - 1) {
                arr[i] = arr[i + 1]
            }
            arr[arr.size - 1] = temp
            --times
        }
        return arr
    }

    /**
     * Inverse rendition of ShiftRows (this time, right rotations are used).
     * @param arr the array to compute right rotations.
     */

    fun invShiftRows(arr: Array<IntArray>) {
        for (i in 1 until arr.size) {
            arr[i] = rightrotate(arr[i], i)
        }
    }

    /**
     * Right reverses the array in a similar fashion as leftrotate
     * @param arr
     * @param times
     * @return
     */

    private fun rightrotate(arr: IntArray, times: Int): IntArray {
        var times = times
        if (arr.size == 0 || arr.size == 1 || times % 4 == 0) {
            return arr
        }
        while (times > 0) {
            val temp = arr[arr.size - 1]
            for (i in arr.size - 1 downTo 1) {
                arr[i] = arr[i - 1]
            }
            arr[0] = temp
            --times
        }
        return arr
    }

    /**
     * Performed by mapping each element in the current matrix with the value
     * returned by its helper function.
     * @param arr the array with we calculate against the galois field matrix.
     */

    fun mixColumns(arr: Array<IntArray>) //method for mixColumns
    {
        val tarr = Array(4) { IntArray(4) }
        for (i in 0..3) {
            System.arraycopy(arr[i], 0, tarr[i], 0, 4)
        }
        for (i in 0..3) {
            for (j in 0..3) {
                arr[i][j] = mcHelper(tarr, galois, i, j)
            }
        }
    }

    /**
     * Helper method of mixColumns in which compute the mixColumn formula on each element.
     * @param arr passed in current matrix
     * @param g the galois field
     * @param i the row position
     * @param j the column position
     * @return the computed mixColumns value
     */

    private fun mcHelper(arr: Array<IntArray>, g: Array<IntArray>, i: Int, j: Int): Int {
        var mcsum = 0
        for (k in 0..3) {
            val a = g[i][k]
            val b = arr[k][j]
            mcsum = mcsum xor mcCalc(a, b)
        }
        return mcsum
    }

    private fun mcCalc(a: Int, b: Int) //Helper method for mcHelper
            : Int {
        if (a == 1) {
            return b
        } else if (a == 2) {
            return MCTables.mc2[b / 16][b % 16]
        } else if (a == 3) {
            return MCTables.mc3[b / 16][b % 16]
        }
        return 0
    }

    fun invMixColumns(arr: Array<IntArray>) {
        val tarr = Array(4) { IntArray(4) }
        for (i in 0..3) {
            System.arraycopy(arr[i], 0, tarr[i], 0, 4)
        }
        for (i in 0..3) {
            for (j in 0..3) {
                arr[i][j] = invMcHelper(tarr, invgalois, i, j)
            }
        }
    }

    private fun invMcHelper(arr: Array<IntArray>, igalois: Array<IntArray>, i: Int, j: Int) //Helper method for invMixColumns
            : Int {
        var mcsum = 0
        for (k in 0..3) {
            val a = igalois[i][k]
            val b = arr[k][j]
            mcsum = mcsum xor invMcCalc(a, b)
        }
        return mcsum
    }

    /**
     * Helper computing method for inverted mixColumns.
     *
     * @param a Row Position of mcX.
     * @param b Column Position of mcX
     * @return the value in the corresponding mcX table based on the a,b coordinates.
     */

    private fun invMcCalc(a: Int, b: Int) //Helper method for invMcHelper
            : Int {
        if (a == 9) {
            return MCTables.mc9[b / 16][b % 16]
        } else if (a == 0xb) {
            return MCTables.mc11[b / 16][b % 16]
        } else if (a == 0xd) {
            return MCTables.mc13[b / 16][b % 16]
        } else if (a == 0xe) {
            return MCTables.mc14[b / 16][b % 16]
        }
        return 0
    }

    /**
     * The keyScheduling algorithm to expand a short key into a number of separate round keys.
     *
     * @param key the key in which key expansion will be computed upon.
     * @return the fully computed expanded key for the AES encryption/decryption.
     */

    fun keySchedule(key: String): Array<IntArray> {

        val binkeysize = key.length * 4
        val colsize = binkeysize + 48 - 32 * (binkeysize / 64 - 2) //size of key scheduling will be based on the binary size of the key.
        val keyMatrix = Array(4) { IntArray(colsize / 4) } //creates the matrix for key scheduling
        var rconpointer = 1
        var t = IntArray(4)
        val keycounter = binkeysize / 32
        var k: Int

        for (i in 0 until keycounter)
        //the first 1 (128-bit key) or 2 (256-bit key) set(s) of 4x4 matrices are filled with the key.
        {
            for (j in 0..3) {
                keyMatrix[j][i] = Integer.parseInt(key.substring(8 * i + 2 * j, 8 * i + (2 * j + 2)), 16)
            }
        }
        var keypoint = keycounter
        while (keypoint < colsize / 4) {
            val temp = keypoint % keycounter
            if (temp == 0) {
                k = 0
                while (k < 4) {
                    t[k] = keyMatrix[k][keypoint - 1]
                    k++
                }
                t = schedule_core(t, rconpointer++)
                k = 0
                while (k < 4) {
                    keyMatrix[k][keypoint] = t[k] xor keyMatrix[k][keypoint - keycounter]
                    k++
                }
                keypoint++
            } else if (temp == 4) {
                k = 0
                while (k < 4) {
                    val hex = keyMatrix[k][keypoint - 1]
                    keyMatrix[k][keypoint] = sbox[hex / 16][hex % 16] xor keyMatrix[k][keypoint - keycounter]
                    k++
                }
                keypoint++
            } else {
                val ktemp = keypoint + 3
                while (keypoint < ktemp) {
                    k = 0
                    while (k < 4) {
                        keyMatrix[k][keypoint] = keyMatrix[k][keypoint - 1] xor keyMatrix[k][keypoint - keycounter]
                        k++
                    }
                    keypoint++
                }
            }
        }
        return keyMatrix
    }

    /**
     * For every (binary key size / 32)th column in the expanded key. We compute a special column
     * using sbox and an XOR of the an rcon number with the first element in the passed array.
     *
     * @param in the array in which we compute the next set of bytes for key expansion
     * @param rconpointer the element in the rcon array with which to XOR the first element in 'in'
     * @return the next column in the key scheduling.
     */

    fun schedule_core(`in`: IntArray, rconpointer: Int): IntArray {
        var `in` = `in`
        `in` = leftrotate(`in`, 1)
        var hex: Int
        for (i in `in`.indices) {
            hex = `in`[i]
            `in`[i] = sbox[hex / 16][hex % 16]
        }
        `in`[0] = `in`[0] xor rcon[rconpointer]
        return `in`
    }

    /**
     * In the AddRoundKey step, the subkey is combined with the state. For each round, a chunk of the key scheduled is pulled; each subkey is the same size as the state. Each element in the byte matrix is XOR'd with each element in the chunk of the expanded key.
     *
     * @param state reference of the matrix in which addRoundKey will be computed upon.
     * @param keymatrix chunk of the expanded key
     */

    fun addRoundKey(bytematrix: Array<IntArray>, keymatrix: Array<IntArray>) {
        for (i in bytematrix.indices) {
            for (j in 0 until bytematrix[0].size) {
                bytematrix[j][i] = bytematrix[j][i] xor keymatrix[j][i]
            }
        }
    }

    companion object {

        /**
         * S-BOX table used for Key Expansion and Sub-Bytes.
         */
        val newline = System.getProperty("line.separator") //The newline for whatever system you choose to run in.
        val sbox = arrayOf(intArrayOf(0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76), intArrayOf(0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0), intArrayOf(0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15), intArrayOf(0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75), intArrayOf(0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84), intArrayOf(0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf), intArrayOf(0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8), intArrayOf(0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2), intArrayOf(0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73), intArrayOf(0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb), intArrayOf(0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79), intArrayOf(0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08), intArrayOf(0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a), intArrayOf(0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e), intArrayOf(0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf), intArrayOf(0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16))
        /**
         * Inverse SBOX table used for invSubBytes
         */
        val invsbox = arrayOf(intArrayOf(0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb), intArrayOf(0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb), intArrayOf(0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e), intArrayOf(0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25), intArrayOf(0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92), intArrayOf(0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84), intArrayOf(0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06), intArrayOf(0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b), intArrayOf(0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73), intArrayOf(0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e), intArrayOf(0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b), intArrayOf(0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4), intArrayOf(0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f), intArrayOf(0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef), intArrayOf(0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61), intArrayOf(0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d))

        /**
         * Galois table used for mixColumns
         */
        val galois = arrayOf(intArrayOf(0x02, 0x03, 0x01, 0x01), intArrayOf(0x01, 0x02, 0x03, 0x01), intArrayOf(0x01, 0x01, 0x02, 0x03), intArrayOf(0x03, 0x01, 0x01, 0x02))

        /**
         * Inverse Galois table used for invMixColumns
         */
        val invgalois = arrayOf(intArrayOf(0x0e, 0x0b, 0x0d, 0x09), intArrayOf(0x09, 0x0e, 0x0b, 0x0d), intArrayOf(0x0d, 0x09, 0x0e, 0x0b), intArrayOf(0x0b, 0x0d, 0x09, 0x0e))

        /**
         * RCon array used for Key Expansion
         */
        val rcon = intArrayOf(0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb)

        internal var key = ""
        internal var iv = ""
        internal var ftw = ""
        internal var keyreader: BufferedReader? = null
        internal var input: BufferedReader? = null
        internal var mode: Mode? = null
        internal var out: FileWriter? = null
        internal var keyFileIndex = 1 //Index where the keyFile argument should be. Used to determines the index of other arguments.



        /**
         * ToString() for the matrix (2D array).
         *
         * @param m reference of the matrix
         * @return the string representation of the matrix.
         */

        fun MatrixToString(m: Array<IntArray>) //takes in a matrix and converts it into a line of 32 hex characters.
                : String {
            var t = ""
            for (i in m.indices) {
                for (j in 0 until m[0].size) {
                    val h = Integer.toHexString(m[j][i]).toUpperCase()
                    if (h.length === 1) {
                        t += '0' + h
                    } else {
                        t += h
                    }
                }
            }
            return t
        }
    }
}