package com.yalantis.glata.camera

import android.opengl.Matrix

/**
 * Camera for 3D scene. Uses depth and perspective in calculations. Same objects with different Z
 * coordinate will not look the same because they have different distance from the camera. If you
 * are not sure whether you need perspective, in most cases you don't. Just use orthographic camera.
 */
class PerspectiveCamera : BaseCamera() {

    override fun setProjectionMatrix() {
        val w = viewportMatrix[2].toFloat()
        val h = viewportMatrix[3].toFloat()
        aspectRatio = w / h

        val horizontalSize = calculateHorizontalSize(aspectRatio)
        val verticalSize = calculateVerticalSize(aspectRatio)

        val horizontalCenter: Float = 0f
        val verticalCenter: Float = 0f

        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.frustumM(projectionMatrix, 0,
                horizontalCenter - align.getLeftMargin(horizontalSize),
                horizontalCenter + align.getRightMargin(horizontalSize),
                verticalCenter - align.getBottomMargin(verticalSize),
                verticalCenter + align.getTopMargin(verticalSize),
                1f, 100f)
    }
}