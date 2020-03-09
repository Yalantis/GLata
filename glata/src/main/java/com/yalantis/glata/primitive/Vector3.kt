package com.yalantis.glata.primitive

import kotlin.math.pow

class Vector3() {

    var x: Float = 0f
        set(value) {
            field = value
            hasChanges = true
        }
    var y: Float = 0f
        set(value) {
            field = value
            hasChanges = true
        }
    var z: Float = 0f
        set(value) {
            field = value
            hasChanges = true
        }

    private var hasChanges = false

    constructor(x: Float, y: Float, z: Float) : this() {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setAll(value: Float) {
        this.x = value
        this.y = value
        this.z = value
    }

    fun setFrom(another: Vector3) {
        x = another.x
        y = another.y
        z = another.z
    }

    fun normalize() {
        var mod = length()

        if (mod != 0f && mod != 1f) {
            mod = 1f / mod
            x *= mod
            y *= mod
            z *= mod
        }
    }

    fun length() = (x * x + y * y + z * z).pow(0.5f)

    fun hasChanges() : Boolean {
        return if (hasChanges) {
            hasChanges = false
            true
        } else {
            false
        }
    }

    fun setHasNoChanges() {
        hasChanges = false
    }

    companion object {
        fun distance(a: Vector3, b: Vector3) : Float =
                ((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z)).pow(0.5f)
    }
}