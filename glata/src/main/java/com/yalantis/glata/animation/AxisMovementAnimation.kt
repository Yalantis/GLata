package com.yalantis.glata.animation

import android.view.animation.Interpolator
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.primitive.Axis

class AxisMovementAnimation(
        private val axis: Axis,
        private var initialPosition: Float,
        private var destinationPosition: Float,
        private var travelTimeMillis: Float) : IAnimation {

    var interpolator: Interpolator? = null
    var isInfinite = false
    var returnToInitialAfterFinished = false

    private var isFinished = false
    private var isReturning = false
    private var currentPos: Float = 0f
    private var isInitialized = false
    private var animStartedAt: Long = 0L

    override fun animate(rendererParams: RendererParams, modelParams: ModelParams, sceneParams: SceneParams) : Boolean {
        init(rendererParams, modelParams)

        if (isFinished) return true

        var progress = (rendererParams.currentTimeMillis - animStartedAt) / travelTimeMillis

        if (progress >= 1f) {
            checkNextAction()
            animStartedAt = rendererParams.currentTimeMillis
        } else {
            interpolator?.apply { progress = getInterpolation(progress) }
            currentPos = initialPosition + (destinationPosition - initialPosition) * progress
        }

        modelParams.transform.setPosition(axis, currentPos)

        return isFinished
    }

    private fun init(rp: RendererParams, mp: ModelParams) {
        if (isInitialized) return

        mp.transform.setPosition(axis, initialPosition)
        animStartedAt = rp.currentTimeMillis
        isInitialized = true
    }

    private fun checkNextAction() {
        when {
            !returnToInitialAfterFinished -> {
                if (isInfinite) {
                    currentPos = initialPosition
                } else {
                    currentPos = destinationPosition
                    isFinished = true
                }
            }
            !isReturning -> {
                isReturning = true
                currentPos = destinationPosition
                destinationPosition = initialPosition
                initialPosition = currentPos
            }
            isInfinite -> {
                isReturning = false
                currentPos = destinationPosition
                destinationPosition = initialPosition
                initialPosition = currentPos
            }
            else -> {
                currentPos = destinationPosition
                isFinished = true
            }
        }
    }

    override fun isFinished(): Boolean = isFinished


}