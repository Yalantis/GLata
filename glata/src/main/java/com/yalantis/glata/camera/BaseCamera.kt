package com.yalantis.glata.camera

import android.opengl.Matrix
import com.yalantis.glata.primitive.Align
import com.yalantis.glata.primitive.Vector3

abstract class BaseCamera {
    val viewportMatrix = IntArray(4) // contains size of the surface view in pixels
    val projectionMatrix = FloatArray(16) // defines how objects in scene are projected on the screen
    val viewMatrix = FloatArray(16) // contains camera settings

    val position = Vector3(0f, 0f, 10f) // position of the camera
    val target = Vector3(0f, 0f, 0f) // point where camera looks
    val upAxis = Vector3(0f, 1f, 0f) // direction vector looking to the top of the screen

    /**
     * Width of the surface view divided by height of the surface view. This value is updated
     * during onSurfaceChanged function of the renderer.
     */
    var aspectRatio: Float = 1f

    protected var align = Align.CENTER
    protected var horizontalSize = 10f
    protected var verticalSize = 0f

    /**
     * Pivot is the center of coordinate system, the point with coordinates (0, 0, 0). By setting
     * alignment of pivot you define where the (0, 0, 0) point of the coordinate system will be
     * located. It will be there no matter how the size or rotation of surface view will change.
     */
    open fun setPivot(align: Align) {
        this.align = align
    }

    /**
     * Define the height of the surface view in units. Think of it as a scale, mosty for orthographic
     * camera. In perspective camera you have other ways to control the scale.
     *
     * For orthographic projection: This function sets how much units will fit in the surface view
     * from top to bottom no matter of the surface view size. Amount of units in horizontal direction
     * may vary depending on surface view size, device screen proportions and screen rotation. You
     * can define only vertical OR horizontal size, not both, so choose wisely.
     *
     * For perspective projection: height of the camera will still have effect on the size of
     * the surface view in units.
     */
    open fun setHorizontalSizeInUnits(size: Float) {
        horizontalSize = size
        verticalSize = 0f
    }

    /**
     * Define the width of the surface view in units. Think of it as a scale, mosty for orthographic
     * camera. In perspective camera you have other ways to control the scale.
     *
     * For orthographic projection: This function sets how much units will fit in the surface view
     * from left to right no matter of the surface view size. Amount of units in vertical direction
     * may vary depending on surface view size, device screen proportions and screen rotation. You
     * can define only vertical OR horizontal size, not both, so choose wisely.
     *
     * For perspective projection: height of the camera will still have effect on the size of
     * the surface view in units.
     */
    open fun setVerticalSizeInUnits(size: Float) {
        verticalSize = size
        horizontalSize = 0f
    }

    abstract fun setProjectionMatrix()

    open fun setViewMatrix() {
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setLookAtM(viewMatrix, 0,
                position.x, position.y, position.z,
                target.x, target.y, target.z,
                upAxis.x, upAxis.y, upAxis.z)
    }

    open fun setViewportMatrix(width: Int, height: Int) {
        viewportMatrix[0] = 0
        viewportMatrix[1] = 0
        viewportMatrix[2] = width
        viewportMatrix[3] = height
    }

    protected fun calculateHorizontalSize(aspectRatio: Float) : Float {
        return if (horizontalSize == 0f) {
            verticalSize * aspectRatio
        } else {
            horizontalSize
        }
    }

    protected fun calculateVerticalSize(aspectRatio: Float) : Float {
        return if (verticalSize == 0f) {
            horizontalSize / aspectRatio
        } else {
            verticalSize
        }
    }
}