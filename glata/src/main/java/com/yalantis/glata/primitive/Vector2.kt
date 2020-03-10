package com.yalantis.glata.primitive

import kotlin.math.pow

class Vector2() {

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

    private var hasChanges = false

    constructor(x: Float, y: Float) : this() {
        this.x = x
        this.y = y
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setAll(value: Float) {
        this.x = value
        this.y = value
    }

    fun setFrom(another: Vector2) {
        x = another.x
        y = another.y
    }

    fun normalize() {
        var mod = length()

        if (mod != 0f && mod != 1f) {
            mod = 1f / mod
            x *= mod
            y *= mod
        }
    }

    fun length() = (x * x + y * y).pow(0.5f)

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
        fun distance(a: Vector2, b: Vector2) : Float =
                ((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y)).pow(0.5f)
    }
}