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
    val viewportWidth: Int
        get() = viewportMatrix[2]
    val viewportHeight: Int
        get() = viewportMatrix[3]

    protected var align = Align.CENTER
    protected var horizontalSize = 10f
    protected var verticalSize = 0f
    protected var clampType = ClampType.NONE

    /**
     * Pivot is the center of coordinate system, the point with coordinates (0, 0, 0). By setting
     * alignment of pivot you define where the (0, 0, 0) point of the coordinate system will be
     * located. It will be there no matter how the size or rotation of surface view changes.
     */
    open fun setPivot(align: Align) {
        this.align = align
    }

    /**
     * Define the height of the surface view in units. Think of it as a scale, mostly for orthographic
     * camera. In perspective camera you have other ways to control the scale.
     *
     * For orthographic projection: This function sets how many units will fit in the surface view
     * from left to right no matter of the surface view size. Amount of units in horizontal direction
     * may vary depending on surface view size, device screen proportions and screen rotation. You
     * can define only vertical OR horizontal size, not both, so choose wisely.
     *
     * For perspective projection: height of the camera will still have effect on the size of
     * the surface view in units.
     */
    open fun setHorizontalSizeInUnits(size: Float) {
        horizontalSize = size
        verticalSize = 0f
        clampType = ClampType.HORIZONTAL
    }

    /**
     * Define the width of the surface view in units. Think of it as a scale, mostly for orthographic
     * camera. In perspective camera you have other ways to control the scale.
     *
     * For orthographic projection: This function sets how many units will fit in the surface view
     * from top to bottom no matter of the surface view size. Amount of units in vertical direction
     * may vary depending on surface view size, device screen proportions and screen rotation. You
     * can define only vertical OR horizontal size, not both, so choose wisely.
     *
     * For perspective projection: height of the camera will still have effect on the size of
     * the surface view in units.
     */
    open fun setVerticalSizeInUnits(size: Float) {
        verticalSize = size
        horizontalSize = 0f
        clampType = ClampType.VERTICAL
    }

    /**
     * Define the size of the area that must be visible inside your surfaceView no matter of its size.
     */
    open fun setMinimumVisibleArea(minWidth: Float, minHeight: Float) {
        horizontalSize = minWidth
        verticalSize = minHeight
        clampType = ClampType.INNER_BORDER
    }

    /**
     * Define the size of the area borders of which must always be outside of the screen. You will
     * not see what is outside this area no matter of the surfaceView size.
     */
    open fun setMaximumVisibleArea(maxWidth: Float, maxHeight: Float) {
        horizontalSize = maxWidth
        verticalSize = maxHeight
        clampType = ClampType.OUTER_BORDER
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
        return when (clampType) {
            ClampType.HORIZONTAL -> horizontalSize
            ClampType.VERTICAL -> verticalSize * aspectRatio
            ClampType.INNER_BORDER -> {
                val adjustedHorizontalSize = verticalSize * aspectRatio
                val adjustedVerticalSize = horizontalSize / aspectRatio

                val horizontallyAdjustedSquare = adjustedHorizontalSize * verticalSize
                val verticallyAdjustedSquare = adjustedVerticalSize * horizontalSize

                if (horizontallyAdjustedSquare > verticallyAdjustedSquare) {
                    adjustedHorizontalSize
                } else {
                    horizontalSize
                }
            }
            ClampType.OUTER_BORDER -> {
                val adjustedHorizontalSize = verticalSize * aspectRatio
                val adjustedVerticalSize = horizontalSize / aspectRatio

                val horizontallyAdjustedSquare = adjustedHorizontalSize * verticalSize
                val verticallyAdjustedSquare = adjustedVerticalSize * horizontalSize

                if (horizontallyAdjustedSquare < verticallyAdjustedSquare) {
                    adjustedHorizontalSize
                } else {
                    horizontalSize
                }
            }
            else -> 0f
        }
    }

    protected fun calculateVerticalSize(aspectRatio: Float) : Float {
        return when (clampType) {
            ClampType.HORIZONTAL -> horizontalSize / aspectRatio
            ClampType.VERTICAL -> verticalSize
            ClampType.INNER_BORDER -> {
                val adjustedHorizontalSize = verticalSize * aspectRatio
                val adjustedVerticalSize = horizontalSize / aspectRatio

                val horizontallyAdjustedSquare = adjustedHorizontalSize * verticalSize
                val verticallyAdjustedSquare = adjustedVerticalSize * horizontalSize

                if (horizontallyAdjustedSquare > verticallyAdjustedSquare) {
                    verticalSize
                } else {
                    adjustedVerticalSize
                }
            }
            ClampType.OUTER_BORDER -> {
                val adjustedHorizontalSize = verticalSize * aspectRatio
                val adjustedVerticalSize = horizontalSize / aspectRatio

                val horizontallyAdjustedSquare = adjustedHorizontalSize * verticalSize
                val verticallyAdjustedSquare = adjustedVerticalSize * horizontalSize

                if (horizontallyAdjustedSquare < verticallyAdjustedSquare) {
                    verticalSize
                } else {
                    adjustedVerticalSize
                }
            }
            else -> 0f
        }
    }

    protected enum class ClampType {
        NONE, HORIZONTAL, VERTICAL, INNER_BORDER, OUTER_BORDER
    }
}