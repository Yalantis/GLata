package com.yalantis.glata.texture

import android.opengl.ETC1Util
import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.util.Logger
import java.io.InputStream

class CompressedEtc1Texture(rp: RendererParams, name: String) : ITexture {

    private var name: String = "texName"

    private var openglId: Int = 0

    private var version: Int = -1

    var isRepeating = false

    init {
        this.name = name
    }

    override fun createTexture(rp: RendererParams) {
        if (version == rp.version) return

        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            if (isRepeating) {
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            } else {
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            }

            loadEtc1Texture(rp)
        } else Logger().log("BitmapTexture: textureHandle is 0!")

        version = rp.version
        openglId = textureHandle[0]
    }

    override fun bind(rp: RendererParams) {
        if (version != rp.version) createTexture(rp)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, openglId)
    }

    override fun unbind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun loadEtc1Texture(rp: RendererParams) {
        val androidId = rp.context.resources.getIdentifier(rp.pathToRaw + name, null, null)
        var inputStream: InputStream? = null

        try {
            inputStream = rp.context.resources.openRawResource(androidId)
        } catch (e: Exception) {
            Logger().log("Texture: Error creating Etc2Texture $name: " + e.localizedMessage)
        }

        if (inputStream != null) {
            try {
                ETC1Util.loadTexture(
                        GLES20.GL_TEXTURE_2D,
                        0,
                        0,
                        GLES20.GL_RGB,
                        GLES20.GL_UNSIGNED_BYTE,
                        inputStream)
            } catch (e: Exception) {
                Logger().log("Texture: Error creating Etc2Texture $name: " + e.localizedMessage)
            }
        }
    }
}