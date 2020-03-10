package com.yalantis.glata.motion.dragbounds

import com.yalantis.glata.motion.boundingshape.BoundingRect
import com.yalantis.glata.primitive.Transform
import com.yalantis.glata.primitive.Vector3

class DragBounds(val leftBound: Float,
                 val rightBound: Float,
                 val topBound: Float,
                 val bottomBound: Float) {

    fun moveInsideBounds(position: Vector3) {
        with(position) {
            if (x < leftBound) x = leftBound
            if (x > rightBound) x = rightBound
            if (y < bottomBound) y = bottomBound
            if (y > topBound) y = topBound
        }
    }

    fun moveInsideBounds(boundingRect: BoundingRect) {
        with(boundingRect) {
            if (position.x - align.getLeftMargin(width) < leftBound)
                position.x = leftBound + align.getLeftMargin(width)
            if (position.x + align.getRightMargin(width) > rightBound)
                position.x = rightBound - align.getRightMargin(width)
            if (position.y - align.getBottomMargin(height) < bottomBound)
                position.y = bottomBound + align.getBottomMargin(height)
            if (position.y + align.getTopMargin(height) > topBound)
                position.y = topBound - align.getTopMargin(height)
        }
    }
}