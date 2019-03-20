package com.yalantis.glata.core.shader

import com.yalantis.glata.shader.DefaultColorShader
import com.yalantis.glata.shader.DefaultTextureShader
import com.yalantis.glata.shader.TextureMixingShader

enum class Shaders {
    DEFAULT_TEXTURE_SHADER {
        override fun createShader() = DefaultTextureShader()
    },
    DEFAULT_COLOR_SHADER {
        override fun createShader() = DefaultColorShader()
    },
    TEXTURE_MIXING_SHADER {
        override fun createShader() = TextureMixingShader()
    };

    abstract fun createShader() : BaseShader
}