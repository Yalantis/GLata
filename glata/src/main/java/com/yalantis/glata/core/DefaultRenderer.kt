package com.yalantis.glata.core

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.primitive.Color
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class DefaultRenderer(
        val rendererParams: RendererParams,
        private var backgroundColor: Color = Color(1f, 1f, 1f, 1f)) : GLSurfaceView.Renderer {

    private var scene: Scene? = null

    constructor(context: Context, backgroundColor: Color = Color(1f, 1f, 1f, 1f)) :
            this(RendererParams(context), backgroundColor)

    fun setScene(scene: Scene) {
        this.scene = scene
        scene.onAttach(rendererParams)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background color of the GLSurfaceView
        GLES20.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)
        // Disable the depth testing. Now what is drawn later will be on top and cover objects
        // that were drawn earlier
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        // Define as front side the side of the triangle where vertices are drawn in
        // counter-clockwise direction
        GLES20.glFrontFace(GLES20.GL_CCW)
        // Tell the renderer that if the triangle is not drawn from both sides, the back side is
        // the one that should be skipped
        GLES20.glCullFace(GLES20.GL_BACK)
        // Every time the device goes to sleep or the app goes to background, GL context can be
        // lost. We increment version of renderer every time this happens. Then every texture,
        // shader and virtual buffer will compare it's own version to renderer's version and will
        // recreate itself if versions don't match.
        rendererParams.version++

        initScene()

        rendererParams.managers.shaderManager.compileShaders(rendererParams)
    }

    override fun onDrawFrame(unused: GL10) {
        rendererParams.updateCurrentTime()
        // Clear the frame before redrawing it
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        drawScene()
        rendererParams.managers.shaderManager.resetCurrentShaderId()
        rendererParams.managers.textureManager.unbindCurrentTexture()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        rendererParams.displayOrientationVersion++
        GLES20.glViewport(0, 0, width, height)
        scene?.onSurfaceChanged(rendererParams, width, height)
    }

    fun onTouchEvent(event: MotionEvent): Boolean = scene?.onTouchEvent(event) ?: false

    private fun initScene() {
        scene?.onSurfaceCreated(rendererParams)
    }

    private fun drawScene() {
        scene?.onDrawFrame(rendererParams)
    }
}