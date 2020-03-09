package com.yalantis.glata.motion.boundingshape

import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.motion.RayPicking
import com.yalantis.glata.primitive.Align
import com.yalantis.glata.primitive.Vector2
import com.yalantis.glata.primitive.Vector3

class BoundingRect(
        var width: Float,
        var height: Float,
        val position: Vector3,
        var align: Align = Align.CENTER) {

    var lastTouchCoords: Vector2? = null
    var lastTouchSceneCoords: Vector2? = null

    fun setSize(width: Float = this.width, height: Float = this.height) {
        this.width = width
        this.height = height
    }

    fun setPosition(x: Float, y: Float, z: Float = position.z) {
        this.position.set(x, y, z)
    }

    fun setPosition(position: Vector3) {
        this.position.setFrom(position)
    }

    private fun findSceneCoordsOfTouch(sp: SceneParams, screenX: Float, screenY: Float): Vector2 {
        return RayPicking().intersectionWithXOY(screenX, screenY, sp.camera.viewMatrix,
                sp.camera.projectionMatrix, sp.camera.viewportMatrix)
    }

    fun checkIfTouched(sp: SceneParams, screenX: Float, screenY: Float): Boolean {
        val touchedCoords = findSceneCoordsOfTouch(sp, screenX, screenY)
        val x = touchedCoords.x
        val y = touchedCoords.y
        val isTouched = x > position.x - align.getLeftMargin(width) &&
                x < position.x + align.getRightMargin(width) &&
                y > position.y - align.getBottomMargin(height) &&
                y < position.y + align.getTopMargin(height)

        if (isTouched) {
            lastTouchCoords = Vector2(screenX, screenY)
            lastTouchSceneCoords = touchedCoords
        }

        return isTouched
    }

}