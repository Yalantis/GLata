package com.yalantis.glata.texture

import android.opengl.GLES30
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.texture.ETC2Util
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.util.Logger
import java.io.InputStream

class CompressedEtc2Texture(rp: RendererParams, name: String) : ITexture {

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

        GLES30.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])

            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

            if (isRepeating) {
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
            } else {
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
            }

            loadEtc2Texture(rp)
        } else Logger().log("BitmapTexture: textureHandle is 0!")

        version = rp.version
        openglId = textureHandle[0]
    }

    override fun bind(rp: RendererParams) {
        if (version != rp.version) createTexture(rp)

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, openglId)
    }

    override fun unbind() {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
    }

    private fun loadEtc2Texture(rp: RendererParams) {
        val androidId = rp.context.resources.getIdentifier(rp.pathToRaw + name, null, null)
        var inputStream: InputStream? = null

        try {
            inputStream = rp.context.resources.openRawResource(androidId)
        } catch (e: Exception) {
            Logger().log("Texture: Error creating Etc2Texture $name: " + e.localizedMessage)
        }

        if (inputStream != null) {
            try {
                val tex = ETC2Util.createTexture(inputStream)
                GLES30.glCompressedTexImage2D(
                        GLES30.GL_TEXTURE_2D,
                        0,
                        tex.compressionFormat,
                        tex.width,
                        tex.height,
                        0,
                        tex.size,
                        tex.data
                )
            } catch (e: Exception) {
                Logger().log("Texture: Error creating Etc2Texture $name: " + e.localizedMessage)
            }
        }
    }
}