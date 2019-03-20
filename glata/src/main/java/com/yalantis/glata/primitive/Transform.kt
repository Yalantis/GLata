package com.yalantis.glata.primitive

import android.opengl.Matrix

class Transform {
    val position = Vector3()
    val rotation = Vector3()
    val scale = Vector3(1f, 1f, 1f)

    val rotationOrder = RotationOrder.XYZ

    var followThis: Transform? = null

    private val modelMatrix = FloatArray(16)

    init {
        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun setFrom(another: Transform) {
        position.setFrom(another.position)
        rotation.setFrom(another.rotation)
        scale.setFrom(another.scale)
    }

    fun setPosition(x: Float, y: Float, z: Float = 0f) {
        position.set(x, y, z)
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.set(x, y, z)
    }

    fun setScale(x: Float, y: Float, z: Float = 0f) {
        scale.set(x, y, z)
    }

    fun setPosition(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> position.x = value
            Axis.Y -> position.y = value
            Axis.Z -> position.z = value
        }
    }

    fun getPosition(axis: Axis): Float {
        return when (axis) {
            Axis.X -> position.x
            Axis.Y -> position.y
            Axis.Z -> position.z
        }
    }

    fun setRotation(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> rotation.x = value
            Axis.Y -> rotation.y = value
            Axis.Z -> rotation.z = value
        }
    }

    fun getRotation(axis: Axis): Float {
        return when (axis) {
            Axis.X -> rotation.x
            Axis.Y -> rotation.y
            Axis.Z -> rotation.z
        }
    }

    fun setScale(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> scale.x = value
            Axis.Y -> scale.y = value
            Axis.Z -> scale.z = value
        }
    }

    fun getScale(axis: Axis): Float {
        return when (axis) {
            Axis.X -> scale.x
            Axis.Y -> scale.y
            Axis.Z -> scale.z
        }
    }

    fun addToPosition(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> position.x += value
            Axis.Y -> position.y += value
            Axis.Z -> position.z += value
        }
    }

    fun addToRotation(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> {
                rotation.x += value
                rotation.x %= 360f
            }
            Axis.Y -> {
                rotation.y += value
                rotation.y %= 360f
            }
            Axis.Z -> {
                rotation.z += value
                rotation.z %= 360f
            }
        }
    }

    fun addToScale(axis: Axis, value: Float) {
        when (axis) {
            Axis.X -> scale.x += value
            Axis.Y -> scale.y += value
            Axis.Z -> scale.z += value
        }
    }

    fun getMatrix(): FloatArray {
        if (hasChanges()) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z)

            when (rotationOrder) {
                RotationOrder.XYZ -> {
                    rotateX()
                    rotateY()
                    rotateZ()
                }
                RotationOrder.XZY -> {
                    rotateX()
                    rotateZ()
                    rotateY()
                }
                RotationOrder.YXZ -> {
                    rotateY()
                    rotateX()
                    rotateZ()
                }
                RotationOrder.YZX -> {
                    rotateY()
                    rotateZ()
                    rotateX()
                }
                RotationOrder.ZXY -> {
                    rotateZ()
                    rotateX()
                    rotateY()
                }
                RotationOrder.ZYX -> {
                    rotateZ()
                    rotateY()
                    rotateX()
                }
            }

            Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z)

            followThis?.let { followThis ->
                Matrix.multiplyMM(modelMatrix, 0, followThis.getMatrix(), 0, modelMatrix, 0)
            }

        }

        return modelMatrix
    }

    private fun rotateX() {
        Matrix.rotateM(modelMatrix, 0, rotation.x, 1f, 0f, 0f)
    }

    private fun rotateY() {
        Matrix.rotateM(modelMatrix, 0, rotation.y, 0f, 1f, 0f)
    }

    private fun rotateZ() {
        Matrix.rotateM(modelMatrix, 0, rotation.z, 0f, 0f, 1f)
    }

    private fun hasChanges(): Boolean {
        if (position.hasChanges() || rotation.hasChanges() || scale.hasChanges()) {
            position.setHasNoChanges()
            rotation.setHasNoChanges()
            scale.setHasNoChanges()
            return true
        } else {
            return false
        }
    }
}