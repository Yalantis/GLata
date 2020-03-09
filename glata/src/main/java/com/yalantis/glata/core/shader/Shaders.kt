package com.yalantis.glata.core.shader

import com.yalantis.glata.shader.*

enum class Shaders {
    DEFAULT_TEXTURE_SHADER {
        override fun createShader() = DefaultTextureShader()
    },
    DEFAULT_COLOR_SHADER {
        override fun createShader() = DefaultColorShader()
    },
    TEXTURE_MIXING_SHADER {
        override fun createShader() = TextureMixingShader()
    },
    BLUR_SHADER {
        override fun createShader() = BlurShader()
    },
    TINTED_TEXTURE_SHADER {
        override fun createShader() = TintedTextureShader()
    };
    abstract fun createShader() : BaseShader
}