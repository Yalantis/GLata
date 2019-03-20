package com.yalantis.glata.core.model

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.SceneParams

interface IModel {
    val mp: ModelParams
    fun initBufferObject(rp: RendererParams)
    fun draw(rp: RendererParams, sp: SceneParams)
}