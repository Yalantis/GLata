package com.yalantis.glata.core

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class MotionableGLSurfaceView : GLSurfaceView {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var renderer: DefaultRenderer? = null

    init {
        setEGLContextClientVersion(2)
        setOnTouchListener { view, event ->
            renderer?.onTouchEvent(event) ?: false
        }
    }

    fun setRenderer(renderer: DefaultRenderer?) {
        this.renderer = renderer
        super.setRenderer(renderer)
    }

}