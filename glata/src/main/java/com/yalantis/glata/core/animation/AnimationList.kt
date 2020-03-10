package com.yalantis.glata.core.animation

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams

class AnimationList : IAnimation {

    private val animations = ArrayList<IAnimation>()

    var removeFromListIfFinished = false
    private var isFinished = false

    override fun animate(rendererParams: RendererParams,
                         modelParams: ModelParams, sceneParams: SceneParams): Boolean {
        var finished = true

        for (animation in animations) {
            animation.animate(rendererParams, modelParams, sceneParams)

            if (animation.isFinished()) {
                if (removeFromListIfFinished) animations.remove(animation)
            } else {
                finished = false
            }
        }

        isFinished = finished

        return isFinished
    }

    override fun isFinished(): Boolean = isFinished

    fun addAnimation(animation: IAnimation) {
        animations.add(animation)
        isFinished = false
    }
}