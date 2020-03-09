package com.yalantis.glata.core

import android.content.Context
import android.opengl.GLES20
import kotlin.math.max

class RendererParams(var context: Context) {

    val packageName: String = context.packageName
    val pathToDrawable = "$packageName:drawable/" // required to load bitmaps from resources by string name
    val pathToRaw = "$packageName:raw/" // required to load raw resources by string name

    /**
     * All managers for this renderer. Usually it is a singleton, but since we can create several
     * GLSurfaceViews in one activity, I've decided to pass it as a parameter to omit conflicts
     * between renderers.
     */
    val managers: Managers = Managers()

    /**
     * Every time the device goes to sleep or the app goes to background, GL context can be
     * lost. We increment version of renderer every time this happens. Then every texture,
     * shader and virtual buffer will compare it's own version to renderer's version and
     * recreate itself if versions don't match.
     */
    var version: Int = 0

    /**
     * We will increment this variable every time device rotates or GLSurfaceView changes it's size.
     * Cameras and motion managers will compare it's version to renderer's orientation version and
     * reinitialize itself if versions don't match.
     */
    var displayOrientationVersion: Int = 0

    /**
     * We measure time passed from last frame to current and store it in a variable.
     * This is helpful when creating animations. Sometimes due to long initialization or switching
     * between apps this time can be too long and animations behave not like you expected. This is
     * why we clamp this time to some max value. You can change it in scene initialization
     * if you want.
     */
    var maxFrameTime: Long = 150L

    /**
     * These are the default parameters for alpha blending equation. They will be applied if your
     * model does not have personal blending parameters. You can set model's personal blending
     * parameters in ModelParams object attached to your model.
     * Most common pairs are:
     * (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA) and
     * (GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
     */
    var blendFuncParams: Pair<Int, Int> = Pair(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

    var currentTimeMillis: Long = System.currentTimeMillis()
        private set

    var deltaTime: Long = 0L
        private set

    var deltaTimeSeconds: Float = 0f
        private set

    var isAnimationEnabled = true

    /**
     * Defines if the renderer must use vertex buffer objects (store the model's data in video
     * memory). If something is really wrong with drawing and you don't know what to do, try to set
     * this as false and follow the result. Otherwise use VBO and be happy, it works faster.
     */
    var isVboEnabled = true

    fun updateCurrentTime() {
        if (isAnimationEnabled) {
            val now = System.currentTimeMillis()
            deltaTime = now - currentTimeMillis

            if (deltaTime > maxFrameTime) deltaTime = maxFrameTime

            deltaTimeSeconds = deltaTime / 1000f
            currentTimeMillis = now
        } else {
            deltaTime = 0L
        }
    }
}