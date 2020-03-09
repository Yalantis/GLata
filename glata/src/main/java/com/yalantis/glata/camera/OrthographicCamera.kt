package com.yalantis.glata.camera

import android.opengl.Matrix

/**
 * Camera for 2D scene, doesn't have perspective and depth. Two rectangles of the same size with
 * different Z coordinate will look the same because depth has no effect here.
 */
class OrthographicCamera : BaseCamera() {

    override fun setProjectionMatrix() {
        if (viewportWidth == 0 && viewportHeight == 0) return

        aspectRatio = viewportWidth.toFloat() / viewportHeight.toFloat()

        val horizontalSize = calculateHorizontalSize(aspectRatio)
        val verticalSize = calculateVerticalSize(aspectRatio)

        val horizontalCenter = 0f
        val verticalCenter = 0f

        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.orthoM(projectionMatrix, 0,
                horizontalCenter - align.getLeftMargin(horizontalSize),
                horizontalCenter + align.getRightMargin(horizontalSize),
                verticalCenter - align.getBottomMargin(verticalSize),
                verticalCenter + align.getTopMargin(verticalSize),
                1f, 100f)
    }
}