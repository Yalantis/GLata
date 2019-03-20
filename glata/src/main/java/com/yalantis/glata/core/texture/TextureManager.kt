package com.yalantis.glata.core.texture

import com.yalantis.glata.core.RendererParams

class TextureManager {

    var currentTextureId: Int = -1
        private set

    private val textures = HashMap<Int, ITexture>()
    private val textureNames = HashMap<String, Int>()

    private var nextId: Int = 0

    fun add(texture: ITexture) : Int {
        nextId++
        textures[nextId] = texture
        return nextId
    }

    fun bind(rp: RendererParams, textureId: Int) {
        textures[textureId]?.bind(rp)
    }

    fun unbindTexture(textureId: Int) {
        if (currentTextureId == 0) return

        currentTextureId = 0

        if (textures.containsKey(textureId))
            textures[textureId]?.unbind()
    }

    fun unbindCurrentTexture() {
        unbindTexture(currentTextureId)
    }
}