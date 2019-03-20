package com.yalantis.glata.primitive

import android.view.animation.Interpolator
import com.yalantis.glata.core.RendererParams

class Transition(private val from: Float,
                 private val to: Float,
                 private val transitionTimeMillis: Float) {

    var value: Float = 0f
        private set

    var progress: Float = 0f
        private set

    var interpolator: Interpolator? = null

    private var isInitialized = false
    private var startTime: Long = 0L

    fun update(rp: RendererParams) {
        init(rp)

        var currentProgress = (rp.currentTimeMillis - startTime) / transitionTimeMillis

        if (currentProgress >= 1f) {
            currentProgress = 1f
            value = to
        } else {
            interpolator?.apply { currentProgress = getInterpolation(currentProgress) }
            value = from + (to - from) * currentProgress
        }

        progress = currentProgress
    }

    private fun init(rp: RendererParams) {
        if (isInitialized) return

        startTime = rp.currentTimeMillis
        isInitialized = true
    }
}