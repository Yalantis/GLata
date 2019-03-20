package com.yalantis.glata.primitive

class Color() {
    var r: Float = 1f
    var g: Float = 1f
    var b: Float = 1f
    var a: Float = 1f

    constructor(r: Int, g: Int, b: Int, a: Int) : this() {
        this.r = r.toFloat() / 255f
        this.g = g.toFloat() / 255f
        this.b = b.toFloat() / 255f
        this.a = a.toFloat() / 255f
    }

    constructor(r: Float, g: Float, b: Float, a: Float) : this() {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    fun set(r: Float, g: Float, b: Float, a: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }
}