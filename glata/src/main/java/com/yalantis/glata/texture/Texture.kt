package com.yalantis.glata.texture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLUtils
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.util.Logger
import java.nio.ByteBuffer
import java.nio.ByteOrder



class Texture(name: String, resId: Int) : ITexture {

    private var name: String = "texName"

    private var androidId = -1

    private var openglId: Int = 0

    private var isMipMapNeeded = true
    private var isPerPixelAlphaFixNeeded = false
    private var version: Int = -1

    var scale: Int = 1
    var isRepeating = false

    init {
        this.name = name
        androidId = resId
    }

    constructor(rp: RendererParams, resId: Int) :
            this(rp.context.resources.getResourceEntryName(resId), resId)

    constructor(rp: RendererParams, name: String) :
            this(name, rp.context.resources.getIdentifier(rp.pathToDrawable + name, null, null))

    private fun restoreBitmap(rp: RendererParams): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeResource(rp.context.resources, androidId, scaleBitmap(scale))
        } catch (e: Exception) {
            Logger().log("Texture: Error creating bitmap $name")
            e.printStackTrace()
        }

        return bitmap
    }

    private fun scaleBitmap(scale: Int) : BitmapFactory.Options{
        val options = BitmapFactory.Options()
        options.inSampleSize = scale
        return options
    }

    override fun createTexture(rp: RendererParams) {
        if (version == rp.version) return

        val bitmap = restoreBitmap(rp) ?: return

        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, if (isMipMapNeeded) GLES20.GL_LINEAR_MIPMAP_NEAREST else GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            if (isRepeating) {
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            } else {
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            }

            if (isPerPixelAlphaFixNeeded) {
                fixAlpha(bitmap)
            } else GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            if (isMipMapNeeded) {
                GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
            }

        } else Logger().log("BitmapTexture: textureHandle is 0!")

        version = rp.version
        cleanup(bitmap)
        openglId = textureHandle[0]
    }

    override fun bind(rp: RendererParams) {
        if (version != rp.version) createTexture(rp)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, openglId)
    }

    override fun unbind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun fixAlpha(bitmap: Bitmap) {
        val imageBuffer = ByteBuffer.allocateDirect(bitmap.height * bitmap.width * 4)
        imageBuffer.order(ByteOrder.nativeOrder())
        val buffer = ByteArray(4)
        for (i in 0..(bitmap.height - 1)) {
            for (j in 0..(bitmap.width)) {
                val color = bitmap.getPixel(j, i)
                buffer[0] = Color.red(color).toByte()
                buffer[1] = Color.green(color).toByte()
                buffer[2] = Color.blue(color).toByte()
                buffer[3] = Color.alpha(color).toByte()
                imageBuffer.put(buffer)
            }
        }
        imageBuffer.position(0)
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                bitmap.width,
                bitmap.height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                imageBuffer)
    }

    private fun cleanup(bitmap: Bitmap) {
        bitmap.recycle()
    }
}