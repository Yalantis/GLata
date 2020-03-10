package com.yalantis.glata.core.scene

import android.view.MotionEvent
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.IModel

abstract class Scene(val sceneParams: SceneParams = SceneParams()) {

    protected val children = ArrayList<IModel>()

    open fun onAttach(rendererParams: RendererParams) {}

    open fun onSurfaceCreated(rendererParams: RendererParams) {
        for (i in children.indices) {
            children[i].initBufferObject(rendererParams)
        }
    }

    open fun onDrawFrame(rendererParams: RendererParams) {
        sceneParams.camera.setViewMatrix()
        for (i in children.indices) {
            children[i].draw(rendererParams, sceneParams)
        }
    }

    open fun onSurfaceChanged(rendererParams: RendererParams, width: Int, height: Int) {
        sceneParams.camera.apply {
            setViewportMatrix(width, height)
            setProjectionMatrix()
        }
    }

    open fun onTouchEvent(event: MotionEvent): Boolean = false

    protected fun addChild(model: IModel) {
        children.add(model)
    }
}