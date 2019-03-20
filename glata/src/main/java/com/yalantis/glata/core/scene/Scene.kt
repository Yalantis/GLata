package com.yalantis.glata.core.scene

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.IModel

abstract class Scene(val sp: SceneParams = SceneParams()) {

    protected val children = ArrayList<IModel>()

    open fun onAttach(rp: RendererParams) {

    }

    open fun onSurfaceCreated(rp: RendererParams) {
        for (i in children.indices) {
            children[i].initBufferObject(rp)
        }
    }

    open fun onDrawFrame(rp: RendererParams) {
        sp.camera.setViewMatrix()

        for (i in children.indices) {
            children[i].draw(rp, sp)
        }
    }

    open fun onSurfaceChanged(rp: RendererParams, width: Int, height: Int) {
        sp.camera.setViewportMatrix(width, height)
        sp.camera.setProjectionMatrix()
    }
}