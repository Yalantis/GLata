package com.yalantis.glata.core.model

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.SceneParams

interface IModel {
    val modelParams: ModelParams
    fun initBufferObject(rendererParams: RendererParams)
    fun draw(rendererParams: RendererParams, sceneParams: SceneParams)
}