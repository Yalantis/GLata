package com.yalantis.glata.texture

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.texture.ITexture

class Multitexture(private val name: String, private val textures: List<ITexture>) : ITexture {

    override fun createTexture(rendererParams: RendererParams) {
        for (texture in textures) {
            texture.createTexture(rendererParams)
        }
    }

    override fun getName(): String = name

    override fun bind(rendererParams: RendererParams) {
        for (i in textures.indices) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            textures[i].bind(rendererParams)
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    }

    override fun unbind() {
        for (i in textures.indices) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            textures[i].unbind()
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    }
}