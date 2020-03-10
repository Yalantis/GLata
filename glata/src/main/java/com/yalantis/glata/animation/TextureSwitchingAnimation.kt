package com.yalantis.glata.animation

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams

class TextureSwitchingAnimation(
        private val textureIds: IntArray,
        private val frameTime: Float
) : IAnimation {

    var isInfinite = true

    private var currentFrame = 0
    private var lastSwitchTime = -1L

    private var isFinished = false

    override fun animate(rendererParams: RendererParams, modelParams: ModelParams, sceneParams: SceneParams): Boolean {
        if (isFinished) return isFinished

        val now = rendererParams.currentTimeMillis

        if (now - lastSwitchTime >= frameTime) {
            currentFrame++
            if (currentFrame >= textureIds.size) {
                if (isInfinite) {
                    currentFrame = 0
                } else {
                    currentFrame = textureIds.size - 1
                    isFinished = true
                }
            }
            modelParams.textureId = textureIds[currentFrame]
            lastSwitchTime = now
        }

        return isFinished
    }

    override fun isFinished(): Boolean = isFinished
}