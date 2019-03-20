package com.yalantis.glata.core

import android.opengl.GLSurfaceView
import android.os.Handler

class FpsStabilizer(private val glSurfaceView: GLSurfaceView, private val fps: Float = 35f) {
    private var handler = Handler()

    private var isPaused: Boolean = false

    private val drawFrameRunnable = Runnable { requestRender() }

    init {
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    private fun requestRender() {
        handler.removeCallbacks(drawFrameRunnable) // remove all delayed draw calls
        if (!isPaused) {
            handler.postDelayed(drawFrameRunnable, (1000f / fps).toLong()) // launch a delayed runnable
            glSurfaceView.requestRender() // render a frame
        }
    }

    /**
     * this should be called in activity's onPause
     */
    fun onPause() {
        isPaused = true
    }

    /**
     * this should be called in activity's onResume
     */
    fun onResume() {
        isPaused = false
        requestRender() // start the rendering process
    }
}