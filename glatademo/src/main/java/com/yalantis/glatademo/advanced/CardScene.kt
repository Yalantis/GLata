package com.yalantis.glatademo.advanced

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.core.shader.Shaders
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.model.Rectangle
import com.yalantis.glata.primitive.Align
import com.yalantis.glata.primitive.Axis
import com.yalantis.glata.primitive.Color

class CardScene(val texture: ITexture) : Scene() {

    private lateinit var background: Rectangle

    override fun onAttach(rendererParams: RendererParams) {
        super.onAttach(rendererParams)
        rendererParams.managers.shaderManager.apply {
            add(Shaders.DEFAULT_TEXTURE_SHADER)
            add(Shaders.TINTED_TEXTURE_SHADER)
        }

        sceneParams.camera.apply {
            setMaximumVisibleArea(10f, 10f)
            setPivot(Align.CENTER)
            setProjectionMatrix()
        }

        background = Rectangle.builder()
                .setSize(12.5f, 12.5f)
                .setVerticalGradientColor(Color(1f, 1f, 1f, 1f), Color(0.2f, 0.2f, 0.2f, 1f))
                .setHasTexture(true)
                .build()
        background.modelParams.apply {
            setShader(Shaders.TINTED_TEXTURE_SHADER)
            textureId = rendererParams.managers.textureManager.add(texture)
        }
        addChild(background)
    }

    fun setScrollPercent(percent: Float) {
        background.modelParams.transform.setPosition(Axis.X, 2f - percent * 4f)
    }
}