package com.yalantis.glata.model

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.Geometry
import com.yalantis.glata.core.model.IModel
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams

open class Model(
        val geometry: Geometry,
        override val modelParams: ModelParams
) : IModel {

    var animation: IAnimation? = null

    constructor() : this(Geometry(), ModelParams())

    override fun initBufferObject(rendererParams: RendererParams) {
        geometry.initBufferObject(rendererParams)
    }

    override fun draw(rendererParams: RendererParams, sceneParams: SceneParams) {
        geometry.prepare(rendererParams, modelParams, sceneParams)
        animation?.animate(rendererParams, modelParams, sceneParams)
        geometry.draw(rendererParams, modelParams, sceneParams)
    }
}