package com.yalantis.glata.model

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.IAnimation
import com.yalantis.glata.core.model.Geometry
import com.yalantis.glata.core.model.IModel
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams

open class Model(val geometry: Geometry, override val mp: ModelParams) : IModel {

    var animation: IAnimation? = null

    constructor() : this(Geometry(), ModelParams())

    override fun initBufferObject(rp: RendererParams) {
        geometry.initBufferObject(rp)
    }

    override fun draw(rp: RendererParams, sp: SceneParams) {
        geometry.prepare(rp, mp, sp)
        animation?.animate(rp, mp, sp)
        geometry.draw(rp, mp, sp)
    }
}