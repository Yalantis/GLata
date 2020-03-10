package com.yalantis.glata.motion.behavior

import com.yalantis.glata.core.model.IModel
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.model.Rectangle
import com.yalantis.glata.motion.RayPicking
import com.yalantis.glata.motion.boundingshape.BoundingRect
import com.yalantis.glata.motion.dragbounds.DragBounds
import com.yalantis.glata.primitive.Vector3

class DraggableObject(
        private val model: IModel,
        private val touchBounds: BoundingRect,
        private val dragBounds: DragBounds? = null
): Motionable {

    var isDraggingNow = false
    val modelInitialPosition = Vector3().apply { setFrom(model.modelParams.transform.position) }

    constructor(model: Rectangle, dragBounds: DragBounds? = null):
            this(model, BoundingRect(model.width, model.height, model.modelParams.transform.position), dragBounds)

    override fun checkIfTouched(sp: SceneParams, x: Float, y: Float): Boolean {
        isDraggingNow = touchBounds.checkIfTouched(sp, x, y)
        if (isDraggingNow) {
            modelInitialPosition.setFrom(model.modelParams.transform.position)
        }
        return isDraggingNow
    }

    override fun onTouch(sp: SceneParams, x: Float, y: Float) {
        if (isDraggingNow) {
            val currentTouchCoords = RayPicking().intersectionWithXOY(x, y, sp.camera.viewMatrix,
                    sp.camera.projectionMatrix, sp.camera.viewportMatrix)

            touchBounds.lastTouchSceneCoords?.let { firstTouchCoords ->
                val diffX = currentTouchCoords.x - firstTouchCoords.x
                val diffY = currentTouchCoords.y - firstTouchCoords.y

                val newX = modelInitialPosition.x + diffX
                val newY = modelInitialPosition.y + diffY

                touchBounds.setPosition(newX, newY)
                dragBounds?.moveInsideBounds(touchBounds)

                model.modelParams.transform.position.setFrom(touchBounds.position)
            }
        }
    }

    override fun onEndTouch(sp: SceneParams, x: Float, y: Float) {
        isDraggingNow = false
    }
}