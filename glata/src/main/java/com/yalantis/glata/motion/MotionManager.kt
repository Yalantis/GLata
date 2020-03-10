package com.yalantis.glata.motion

import android.view.MotionEvent
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.motion.behavior.Motionable

class MotionManager(private val maxPointersOnScreen: Int = 1) {

    private val motionableItems = ArrayList<Motionable>()

    // ---------- MOTION VARIABLES ---------------
    private var isSomeItemTouched = false
    private var needToUpdatePointerIndexes = false

    private val touchedElement = arrayOfNulls<Motionable?>(maxPointersOnScreen)
    private val pointerInAction = BooleanArray(maxPointersOnScreen)
    private val pointerIndex = IntArray(maxPointersOnScreen)
    private val pointerId = IntArray(maxPointersOnScreen)

    // ----------------- OTHER -------------------
    private var surfaceViewHeight: Int = 0
    private var orientationVersion = -1

    fun addMotionable(motionableObject: Motionable) {
        motionableItems.add(motionableObject)
    }

    fun onSurfaceChanged(rp: RendererParams, width: Int, height: Int) {
        if (orientationVersion != rp.displayOrientationVersion) {
            orientationVersion = rp.displayOrientationVersion
            surfaceViewHeight = height
        }
    }

    fun onTouchEvent(sp: SceneParams, e: MotionEvent): Boolean {
        if (motionableItems.isEmpty()) return false

        if (needToUpdatePointerIndexes) updatePointerIndexes(e)

        when (e.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleActionDown(sp, e)
            MotionEvent.ACTION_MOVE -> handleActionMove(sp, e)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> handleActionUp(sp, e)
        }

        return isSomeItemTouched
    }

    private fun handleActionDown(sp: SceneParams, e: MotionEvent) {
        for (i in 0 until maxPointersOnScreen) {
            if (!pointerInAction[i]) {
                val pointerIndex = getPointerIndex(e)
                val touchedItem = checkIfTouchedSomething(sp, e.x, invertY(e.y))
                if (touchedItem != null) {
                    pointerInAction[i] = true
                    this.pointerIndex[i] = pointerIndex
                    pointerId[i] = e.getPointerId(pointerIndex)
                    isSomeItemTouched = true
                }
                touchedElement[i] = touchedItem
                updatePointerIndexes(e)
            }
        }
    }

    private fun handleActionMove(sp: SceneParams, e: MotionEvent) {
        for (i in 0 until maxPointersOnScreen) {
            if (pointerInAction[i]) {
                touchedElement[i]?.onTouch(sp, e.getX(pointerIndex[i]), invertY(e.getY(pointerIndex[i])))
                isSomeItemTouched = true
            }
        }
    }

    private fun handleActionUp(sp: SceneParams, e: MotionEvent) {
        val pointerIndex = getPointerIndex(e)
        val pointerId = e.getPointerId(pointerIndex)
        for (i in 0 until maxPointersOnScreen) {
            if (pointerInAction[i] && this.pointerId[i] == pointerId) {
                pointerInAction[i] = false
                touchedElement[i]?.onEndTouch(sp, e.getX(pointerIndex), invertY(e.getY(pointerIndex)))
                touchedElement[i] = null
                break
            }
        }

        if (e.pointerCount > 0) needToUpdatePointerIndexes = true
    }

    private fun getPointerIndex(e: MotionEvent): Int {
        return e.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    }

    private fun updatePointerIndexes(e: MotionEvent) {
        var pointersOnUI = 0

        for (i in 0 until e.pointerCount) {
            val id = e.getPointerId(i)
            for (j in 0 until maxPointersOnScreen) {
                if (pointerInAction[j]) {
                    if (pointerId[j] == id) {
                        pointerIndex[j] = i
                    }
                    pointersOnUI++
                }
            }
        }

        if (pointersOnUI == 0) {
            isSomeItemTouched = false
        }
        needToUpdatePointerIndexes = false
    }

    private fun invertY(y: Float): Float {
        return surfaceViewHeight - y
    }

    private fun checkIfTouchedSomething(sp: SceneParams, touchX: Float, touchY: Float): Motionable? {
        for (item in motionableItems) {
            if (item.checkIfTouched(sp, touchX, touchY)) {
                isSomeItemTouched = true
                return item
            }
        }
        return null
    }
}