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
        val id = nextId
        textures[id] = texture
        if (texture.getName().isNotEmpty()) {
            textureNames[texture.getName()] = id
        }
        return id
    }

    fun getId(textureName: String) = textureNames[textureName] ?: -1

    fun bind(rp: RendererParams, textureId: Int) {
//        if (currentTextureId != textureId && currentTextureId != -1) {
//            textures[currentTextureId]?.unbind()
//        }
        textures[textureId]?.let {
            currentTextureId = textureId
            it.bind(rp)
        }
    }

    fun unbindTexture(textureId: Int) {
        if (currentTextureId == -1) return

        textures[textureId]?.unbind()

        currentTextureId = -1
    }

    fun unbindCurrentTexture() {
        unbindTexture(currentTextureId)
    }
}