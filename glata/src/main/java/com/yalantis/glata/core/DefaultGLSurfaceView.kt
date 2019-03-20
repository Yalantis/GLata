package com.yalantis.glata.core

import android.content.Context
import android.opengl.GLSurfaceView

class DefaultGLSurfaceView(context: Context, renderer: GLSurfaceView.Renderer) : GLSurfaceView(context) {

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}