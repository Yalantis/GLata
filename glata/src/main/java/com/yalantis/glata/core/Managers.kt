package com.yalantis.glata.core

import com.yalantis.glata.core.shader.ShaderManager
import com.yalantis.glata.core.texture.TextureManager

class Managers(
        val textureManager: TextureManager,
        val shaderManager: ShaderManager
) {
    constructor() : this(TextureManager(), ShaderManager())
}