package com.yalantis.glata.util

object Utils {
    const val DEG_TO_RAD: Float = Math.PI.toFloat() / 180f
    const val RAD_TO_DEG: Float = 180f / Math.PI.toFloat()

    fun clamp(min: Float, max: Float, value: Float) : Float {
        when {
            min > max -> Logger().log("clamp failed: $min !<= $max")
            value < min -> return min
            value > max -> return max
        }
        return value
    }

    fun clamp(min: Int, max: Int, value: Int) : Int {
        when {
            min > max -> Logger().log("clamp failed: $min !<= $max")
            value < min -> return min
            value > max -> return max
        }
        return value
    }
}