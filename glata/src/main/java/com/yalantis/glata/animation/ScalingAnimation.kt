package com.yalantis.glata.animation

import android.view.animation.Interpolator
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.primitive.Axis

class ScalingAnimation(
        private var startScalingFrom: Float,
        private var scaleTo: Float,
        private val scaleTimeMillis: Float,
        private val scaleAxis: Axis? = null) : IAnimation {

    private var scaleDifference: Float = 0f
    private var isIncreasing: Boolean = true
    private var isFinished = false
    private var isInitialized = false
    private var animStartedAt: Long = 0L
    var isInfinite = false

    var interpolator: Interpolator? = null

    override fun animate(rp: RendererParams, mp: ModelParams, sp: SceneParams): Boolean {
        init(rp, mp)
        doAnimate(rp, mp)

        return isFinished
    }

    private fun init(rp: RendererParams, mp: ModelParams) {
        if (!isInitialized) {
            isIncreasing = (startScalingFrom < scaleTo)

            setScale(mp, startScalingFrom)

            scaleDifference = Math.max(startScalingFrom, scaleTo) - Math.min(startScalingFrom, scaleTo)

            animStartedAt = rp.currentTimeMillis
            isInitialized = true
        }
    }

    private fun checkNextAction() {
            if (isInfinite) {
                val tmp = scaleTo
                scaleTo = startScalingFrom
                startScalingFrom = tmp
                isIncreasing = !isIncreasing
            } else {
                isFinished = true
            }
    }

    private fun doAnimate(rp: RendererParams, mp: ModelParams) {
        var progress = (rp.currentTimeMillis - animStartedAt) / scaleTimeMillis

        if (progress >= 1f) {
            setScale(mp, scaleTo)
            checkNextAction()
            animStartedAt = rp.currentTimeMillis
        } else {
            interpolator?.apply { progress = getInterpolation(progress) }
            val newScale = startScalingFrom + (scaleTo - startScalingFrom) * progress
            setScale(mp, newScale)
        }
    }

    private fun setScale(mp: ModelParams, scale: Float) {
        if (scaleAxis != null) {
            mp.transform.setScale(scaleAxis, scale)
        } else {
            mp.transform.scale.setAll(scale)
        }
    }

    override fun isFinished(): Boolean = isFinished
}