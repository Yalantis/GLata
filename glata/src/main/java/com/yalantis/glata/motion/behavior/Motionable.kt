package com.yalantis.glata.motion.behavior

import com.yalantis.glata.core.scene.SceneParams

interface Motionable {

    fun checkIfTouched(sp: SceneParams, x: Float, y: Float): Boolean

    fun onTouch(sp: SceneParams, x: Float, y: Float)

    fun onEndTouch(sp: SceneParams, x: Float, y: Float)
}