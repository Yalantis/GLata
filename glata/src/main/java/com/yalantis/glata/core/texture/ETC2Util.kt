package com.yalantis.glata.core.texture

import android.annotation.TargetApi
import android.opengl.ETC1
import android.opengl.ETC1Util
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.os.Build
import android.util.Log
import com.yalantis.glata.util.Logger
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

//I've made some improvements because this didn't work with GL_COMPRESSED_RGBA8_ETC2_EAC format.
/**
 * All in one utility class for ETC2 textures. This performs the same duties as the [ETC1] and [ETC1Util]
 * classes, but for the ETC2 format. Can also handle ETC1 textures. For more information see the OpenGL ES 3.0 specification.
 *
 * @author Jared Woolston (jwoolston@tenkiv.com)
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
object ETC2Util {

    /**
     * The original ETC1 format is also compatible with ETC2 decoders.
     */
    const val GL_COMPRESSED_ETC1_RGB8_OES = GLES11Ext.GL_ETC1_RGB8_OES

    /**
     * The main difference is that the newer version contains three new modes; the ‘T-mode’
     * and the ‘H-mode’ which are good for sharp chrominance blocks and the ‘Planar’ mode which
     * is good for smooth blocks.
     *
     * NOTE: ETC1 files can be passed to ETC2 decoders as [GLES30.GL_COMPRESSED_RGB8_ETC2].
     */
    const val GL_COMPRESSED_RGB8_ETC2 = GLES30.GL_COMPRESSED_RGB8_ETC2

    /**
     * Same as [.GL_COMPRESSED_RGB8_ETC2] with the difference that the values should be interpreted as sRGB-values
     * instead of RGB values.
     */
    const val GL_COMPRESSED_SRGB8_ETC2 = GLES30.GL_COMPRESSED_SRGB8_ETC2

    /**
     * Encodes RGBA8 data. The RGB part is encoded exactly the same way as [.GL_COMPRESSED_RGB8_ETC2].
     * The alpha part is encoded separately.
     */
    const val GL_COMPRESSED_RGBA8_ETC2_EAC = GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC

    /**
     * The same as [.GL_COMPRESSED_RGBA8_ETC2_EAC] but here the RGB-values (but not the alpha value) should be
     * interpreted as sRGB-values.
     */
    const val GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = GLES30.GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC

    /**
     * A one-channel unsigned format. It is similar to the alpha part of [.GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC]
     * but not exactly the same - it delivers higher precision. It is possible to make hardware that can decode both
     * formats with minimal overhead.
     */
    const val GL_COMPRESSED_R11_EAC = GLES30.GL_COMPRESSED_R11_EAC

    /**
     * A two-channel unsigned format. Each channel is decoded exactly as [.GL_COMPRESSED_R11_EAC].
     */
    const val GL_COMPRESSED_RG11_EAC = GLES30.GL_COMPRESSED_RG11_EAC

    /**
     * A one-channel signed format. This is good in situations when it is important to be able to preserve zero
     * exactly, and still use both positive and negative values. It is designed to be similar enough to
     * [.GL_COMPRESSED_R11_EAC] so that hardware can decode both with minimal overhead, but it is not exactly
     * the same. For details, see the corresponding sections of the OpenGL ES 3.0 spec.
     */
    const val GL_COMPRESSED_SIGNED_R11_EAC = GLES30.GL_COMPRESSED_SIGNED_R11_EAC

    /**
     * A two-channel signed format. Each channel is decoded exactly as [.GL_COMPRESSED_SIGNED_R11_EAC].
     */
    const val GL_COMPRESSED_SIGNED_RG11_EAC = GLES30.GL_COMPRESSED_SIGNED_RG11_EAC

    /**
     * Very similar to [.GL_COMPRESSED_RGB8_ETC2], but has the ability to represent “punchthrough”-alpha
     * (completely opaque or transparent). Each block can select to be completely opauqe using one bit. To fit
     * this bit, there is no individual mode in [.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2]. In other
     * respects, the opaque blocks are decoded as in [.GL_COMPRESSED_RGB8_ETC2]. For the transparent blocks,
     * one index is reserved to represent transparency, and the decoding of the RGB channels are also affected.
     * For details, see the corresponding sections of the OpenGL ES 3.0 spec.
     */
    const val GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = GLES30.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2

    /**
     * The same as [.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2] but should be interpreted as sRGB.
     */
    const val GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = GLES30.GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2

    /**
     * A utility class encapsulating a compressed ETC2 texture.
     *
     * @author Jared Woolston (jwoolston@tenkiv.com)
     */
    class ETC2Texture(
            /**
             * Get the ETC2 compression type for this texture.
             *
             * @return `int` One of the GL_COMPRESSED_* integers for ETC2.
             */
            val compressionFormat: Int,
            /**
             * Get the width of the texture in pixels.
             *
             * @return the width of the texture in pixels.
             */
            val width: Int,
            /**
             * Get the height of the texture in pixels.
             *
             * @return the height of the texture in pixels.
             */
            val height: Int,
            /**
             * Get the size of the texture in bytes not including header. This is what should be
             * passed in glCompressedTexImage2D.
             *
             * @return the size of the texture in bytes.
             */
            val size: Int,
            /**
             * Get the compressed data of the texture.
             *
             * @return the texture data.
             */
            val data: ByteBuffer)

    /**
     * Create a new ETC2Texture from an input stream containing a PKM formatted compressed texture.
     *
     * @param input an input stream containing a PKM formatted compressed texture.
     *
     * @return an ETC2Texture read from the input stream.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createTexture(input: InputStream): ETC2Texture {
        val width: Int
        val height: Int
        val format: Int
        val ioBuffer = ByteArray(4096)

        // We can use the ETC1 header size as it is the same
        if (input.read(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE) != ETC1.ETC_PKM_HEADER_SIZE) {
            throw IOException("Unable to read PKM file header.")
        }
        val headerBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE)
                .order(ByteOrder.BIG_ENDIAN)
        headerBuffer.put(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE).position(0)
        if (!ETC2.isValid(headerBuffer)) {
            throw IOException("Not a PKM file.")
        }
        width = ETC2.getWidth(headerBuffer)
        height = ETC2.getHeight(headerBuffer)
        val encodedWidth = ETC2.getEncodedWidth(headerBuffer)
        val encodedHeight = ETC2.getEncodedHeight(headerBuffer)
        format = ETC2.getETC2CompressionType(headerBuffer)

        val encodedSize = ETC2.getEncodedDataSize(encodedWidth, encodedHeight, format)

        //val size = (encodedWidth + 3 and 3.inv()) * (encodedHeight + 3 and 3.inv())
        //Log.e("Etc", "w $width, h $height, ew $encodedWidth, eh $encodedHeight, s $size, es $encodedSize")

        val dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.LITTLE_ENDIAN)
        var i = 0
        while (i < encodedSize) {
            val chunkSize = Math.min(ioBuffer.size, encodedSize - i)
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw IOException("Unable to read PKM file data.")
            }
            dataBuffer.put(ioBuffer, 0, chunkSize)
            i += chunkSize
        }
        dataBuffer.position(0)
        return ETC2Texture(format, width, height, encodedSize, dataBuffer)
    }

    /**
     * Parsing and data utility class for ETC2 textures.
     *
     * @author Jared Woolston (jwoolston@tenkiv.com)
     */
    object ETC2 {

        /**
         * The magic sequence for an ETC1 file.
         */
        private val ETC1Magic = byteArrayOf(
                0x50, //'P'
                0x4B, //'K'
                0x4D, //'M'
                0x20, //' '
                0x31, //'1'
                0x30  //'0'
        )

        /**
         * The magic sequence for an ETC2 file.
         */
        private val ETC2Magic = byteArrayOf(
                0x50, //'P'
                0x4B, //'K'
                0x4D, //'M'
                0x20, //' '
                0x32, //'2'
                0x30  //'0'
        )

        /**
         * File header offsets.
         */
        private const val ETC2_PKM_FORMAT_OFFSET = 6
        private const val ETC2_PKM_ENCODED_WIDTH_OFFSET = 8
        private const val ETC2_PKM_ENCODED_HEIGHT_OFFSET = 10
        private const val ETC2_PKM_WIDTH_OFFSET = 12
        private const val ETC2_PKM_HEIGHT_OFFSET = 14

        /**
         * These are the supported PKM format identifiers for the PKM header. The sRGB formats are missing here because I was
         * not able to get header file information for them. This is the only thing preventing them from being supported.
         */
        private const val ETC1_RGB8_OES: Short = 0x0000
        private const val RGB8_ETC2: Short = 0x0001
        private const val RGBA8_ETC2_EAC: Short = 0x0003
        private const val RGB8_PUNCHTHROUGH_ALPHA1_ETC2: Short = 0x0004
        private const val R11_EAC: Short = 0x0005
        private const val RG11_EAC: Short = 0x0006
        private const val SIGNED_R11_EAC: Short = 0x0007
        private const val SIGNED_RG11_EAC: Short = 0x0008

        /**
         * Checks the provided file header and determines if this is a valid ETC2 file.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `boolean` True if the file header is valid.
         */
        fun isValid(header: ByteBuffer): Boolean {
            // First check the ETC2 magic sequence
            if (ETC2Magic[0] != header.get(0) && ETC2Magic[1] != header.get(1) && ETC2Magic[2] != header.get(2)
                    && ETC2Magic[3] != header.get(3) && ETC2Magic[4] != header.get(4) && ETC2Magic[5] != header.get(5)) {
                Logger.log("ETC2 header failed magic sequence check.")
                // Check to see if we are ETC1 instead
                if (ETC1Magic[0] != header.get(0) && ETC1Magic[1] != header.get(1) && ETC1Magic[2] != header.get(2)
                        && ETC1Magic[3] != header.get(3) && ETC1Magic[4] != header.get(4) && ETC1Magic[5] != header.get(5)) {
                    Logger.log("ETC1 header failed magic sequence check.")
                    return false
                }
            }

            // Second check the type
            val ETC2_FORMAT = header.getShort(ETC2_PKM_FORMAT_OFFSET)
            when (ETC2_FORMAT) {
                ETC1_RGB8_OES, RGB8_ETC2, RGBA8_ETC2_EAC, RGB8_PUNCHTHROUGH_ALPHA1_ETC2, R11_EAC, RG11_EAC, SIGNED_R11_EAC, SIGNED_RG11_EAC -> {
                }
                else -> {
                    Logger.log("ETC2 header failed format check.")
                    return false
                }
            }

            val encodedWidth = getEncodedWidth(header)
            val encodedHeight = getEncodedHeight(header)
            val width = getWidth(header)
            val height = getHeight(header)

            // Check the width
            if (encodedWidth < width || encodedWidth - width > 4) {
                Logger.log("ETC2 header failed width check. Encoded: $encodedWidth Actual: $width")
                return false
            }
            // Check the height
            if (encodedHeight < height || encodedHeight - height > 4) {
                Logger.log("ETC2 header failed height check. Encoded: $encodedHeight Actual: $height")
                return false
            }

            // We passed all the checks, return true
            return true
        }

        /**
         * Retrieves the particular compression format for the ETC2 Texture.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `int` One of the GL_COMPRESSED_* types for ETC2, or -1 if unrecognized.
         */
        fun getETC2CompressionType(header: ByteBuffer): Int {
            when (header.getShort(ETC2_PKM_FORMAT_OFFSET)) {
                ETC1_RGB8_OES -> return GL_COMPRESSED_ETC1_RGB8_OES
                RGB8_ETC2 -> return GL_COMPRESSED_RGB8_ETC2
                RGBA8_ETC2_EAC -> return GL_COMPRESSED_RGBA8_ETC2_EAC
                RGB8_PUNCHTHROUGH_ALPHA1_ETC2 -> return GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2
                R11_EAC -> return GL_COMPRESSED_R11_EAC
                RG11_EAC -> return GL_COMPRESSED_RG11_EAC
                SIGNED_R11_EAC -> return SIGNED_R11_EAC.toInt()
                SIGNED_RG11_EAC -> return SIGNED_RG11_EAC.toInt()
                else -> return -1
            }
        }

        /**
         * Retrieve the actual texture width in pixels.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `int` The actual texture width.
         */
        fun getWidth(header: ByteBuffer): Int {
            return 0xFFFF and header.getShort(ETC2_PKM_WIDTH_OFFSET).toInt()
        }

        /**
         * Retrieve the actual texture height in pixels.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `int` The actual texture height.
         */
        fun getHeight(header: ByteBuffer): Int {
            return 0xFFFF and header.getShort(ETC2_PKM_HEIGHT_OFFSET).toInt()
        }

        /**
         * Retrieve the encoded texture width in pixels.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `int` The encoded texture width.
         */
        fun getEncodedWidth(header: ByteBuffer): Int {
            return 0xFFFF and header.getShort(ETC2_PKM_ENCODED_WIDTH_OFFSET).toInt()
        }

        /**
         * Retrieve the encoded texture height in pixels.
         *
         * @param header [ByteBuffer] The PKM file header.
         * @return `int` The encoded texture height.
         */
        fun getEncodedHeight(header: ByteBuffer): Int {
            return 0xFFFF and header.getShort(ETC2_PKM_ENCODED_HEIGHT_OFFSET).toInt()
        }

        /**
         * Return the size of the encoded image data (does not include the size of the PKM header).
         *
         * @param width `int` The actual texture width in pixels.
         * @param height `int` The actual texture height in pixels.
         * @param format `int` Compression format of the texture. RGBA8 has larger size and has to
         * be counted in slightly another way.
         * @return `int` The number of bytes required to encode this data.
         */
        fun getEncodedDataSize(width: Int, height: Int, format: Int): Int {
            var size = (width + 3 and 3.inv()) * (height + 3 and 3.inv())
            if (format != GL_COMPRESSED_RGBA8_ETC2_EAC) size = size shr 1
            return size
        }
    }
}