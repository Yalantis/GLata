package com.yalantis.glata.animation

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.primitive.Axis

class AxisRotationAnimation(
        private val speed: Float,
        private val axis: Axis,
        private val speed2: Float,
        private val axis2: Axis?
) : IAnimation {

    constructor(speed: Float, axis: Axis = Axis.Z) : this(speed, axis, 0f, null)

    override fun animate(rp: RendererParams, mp: ModelParams, sp: SceneParams): Boolean {
        animateSingleAxis(rp, mp, axis, speed)
        if (axis2 != null) animateSingleAxis(rp, mp, axis2, speed2)
        return true
    }

    private fun animateSingleAxis(rp: RendererParams, mp: ModelParams, axis: Axis, speed: Float) {
        val deltaAngle = speed * rp.deltaTimeSeconds
        mp.transform.addToRotation(axis, deltaAngle)
    }

    override fun isFinished(): Boolean = true
}