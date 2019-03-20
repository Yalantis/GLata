package com.yalantis.glata.texture

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.texture.ITexture

class Multitexture(private val textures: List<ITexture>) : ITexture {

    override fun createTexture(rp: RendererParams) {
        for (texture in textures) {
            texture.createTexture(rp)
        }
    }

    override fun bind(rp: RendererParams) {
        for (i in textures.indices) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
            textures[i].bind(rp)
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