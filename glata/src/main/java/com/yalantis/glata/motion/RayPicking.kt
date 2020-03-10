package com.yalantis.glata.motion

import android.opengl.GLU
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.primitive.Vector2
import com.yalantis.glata.primitive.Vector3

class RayPicking {

    fun intersection(
            tapX: Float,
            tapY: Float,
            clippingPlane: ClippingPlane,
            viewMatrix: FloatArray,
            projectionMatrix: FloatArray,
            viewportMatrix: IntArray
    ): Vector3 {
        val intersectionCoords = FloatArray(4)

        GLU.gluUnProject(
                tapX,
                tapY,
                clippingPlane.getValue(),
                viewMatrix, 0,
                projectionMatrix, 0,
                viewportMatrix, 0,
                intersectionCoords, 0)

        if (intersectionCoords[3] != 0f) {
            intersectionCoords[0] = intersectionCoords[0] / intersectionCoords[3]
            intersectionCoords[1] = intersectionCoords[1] / intersectionCoords[3]
            intersectionCoords[2] = intersectionCoords[2] / intersectionCoords[3]
        }

        return Vector3(intersectionCoords[0], intersectionCoords[1], intersectionCoords[2])
    }

    fun intersectionWithXOY(tapX: Float, tapY: Float, scene: Scene): Vector2 {
        return with (scene.sceneParams.camera) {
            intersectionWithXOY(tapX, tapY, viewMatrix, projectionMatrix, viewportMatrix)
        }
    }

    fun intersectionWithXOY(
            tapX: Float,
            tapY: Float,
            viewMatrix: FloatArray,
            projectionMatrix: FloatArray,
            viewportMatrix: IntArray
    ): Vector2 {
        val near = intersection(tapX, tapY, ClippingPlane.NEAR, viewMatrix, projectionMatrix, viewportMatrix)
        val far = intersection(tapX, tapY, ClippingPlane.FAR, viewMatrix, projectionMatrix, viewportMatrix)

        // since this is an intersection with XOY plane, Z coordinate of the intersection point is 0.
        // We can use it in canonical line equation to find X and Y coordinates
        val intersectionZ = -near.z / (far.z - near.z)
        val intersectionX = intersectionZ * (far.x - near.x) + near.x
        val intersectionY = intersectionZ * (far.y - near.y) + near.y

        return Vector2(intersectionX, intersectionY)
    }

    enum class ClippingPlane {
        NEAR {
            override fun getValue(): Float = 0f
        },
        FAR {
            override fun getValue(): Float = 1f
        };

        abstract fun getValue(): Float
    }
}