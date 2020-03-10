package com.yalantis.glata.util

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import com.yalantis.glata.core.MotionableGLSurfaceView
import com.yalantis.glata.core.DefaultRenderer
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.primitive.Color

fun GLSurfaceView.initSurface(rendererParams: RendererParams, scene: Scene, backgroundColor: Color = Color(1f, 1f, 1f, 1f)) {
    setEGLContextClientVersion(3)
    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    holder.setFormat(PixelFormat.RGBA_8888)
    val renderer = DefaultRenderer(rendererParams, backgroundColor)
    renderer.setScene(scene)
    setRenderer(renderer)
}

fun GLSurfaceView.initSurfaceTransparent(rendererParams: RendererParams, scene: Scene, backgroundColor: Color = Color(0f, 0f, 0f, 0f)) {
    setEGLContextClientVersion(3)
    setZOrderOnTop(true)
    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    holder.setFormat(PixelFormat.RGBA_8888)
    val renderer = DefaultRenderer(rendererParams, backgroundColor)
    renderer.setScene(scene)
    setRenderer(renderer)
}

fun GLSurfaceView.initSurface(context: Context, scene: Scene, backgroundColor: Color = Color(1f, 1f, 1f, 1f)) {
    setEGLContextClientVersion(3)
    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    holder.setFormat(PixelFormat.RGBA_8888)
    val renderer = DefaultRenderer(context, backgroundColor)
    renderer.setScene(scene)
    setRenderer(renderer)
}

fun MotionableGLSurfaceView.initSurface(context: Context, scene: Scene, backgroundColor: Color = Color(1f, 1f, 1f, 1f)) {
    setEGLContextClientVersion(3)
    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    holder.setFormat(PixelFormat.RGBA_8888)
    val renderer = DefaultRenderer(context, backgroundColor)
    renderer.setScene(scene)
    setRenderer(renderer)
}

fun GLSurfaceView.initSurfaceTransparent(context: Context, scene: Scene, backgroundColor: Color = Color(0f, 0f, 0f, 0f)) {
    setEGLContextClientVersion(3)
    setZOrderOnTop(true)
    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
    holder.setFormat(PixelFormat.RGBA_8888)
    val renderer = DefaultRenderer(context, backgroundColor)
    renderer.setScene(scene)
    setRenderer(renderer)
}