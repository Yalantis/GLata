package com.yalantis.glata.camera

import android.opengl.Matrix

/**
 * Camera for 2D scene, doesn't have perspective and depth. Two rectangles of the same size with
 * different Z coordinate will look the same because depth has no effect here.
 */
class OrthographicCamera : BaseCamera() {

    override fun setProjectionMatrix() {
        if (viewportMatrix[2] == 0 && viewportMatrix[3] == 0) return

        val w = viewportMatrix[2].toFloat()
        val h = viewportMatrix[3].toFloat()
        aspectRatio = w / h

        val horizontalSize = calculateHorizontalSize(aspectRatio)
        val verticalSize = calculateVerticalSize(aspectRatio)

        val horizontalCenter: Float = 0f
        val verticalCenter: Float = 0f

        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.orthoM(projectionMatrix, 0,
                horizontalCenter - align.getLeftMargin(horizontalSize),
                horizontalCenter + align.getRightMargin(horizontalSize),
                verticalCenter - align.getBottomMargin(verticalSize),
                verticalCenter + align.getTopMargin(verticalSize),
                1f, 100f)
    }
}