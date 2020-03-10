package com.yalantis.glata.core

import com.yalantis.glata.util.Logger

class FpsCounter {
    private var frameCount: Int = 0
    private var startTime: Long = System.nanoTime()
    private var fps: Double = 0.0

    fun countFps(logFps: Boolean = true) : Double {
        frameCount++
        if (frameCount % 50 == 0) {
            val now = System.nanoTime()
            val elapsedS = (now - startTime) / 1.0e9
            val msPerFrame = 1000.0 * elapsedS / frameCount
            fps = 1000.0 / msPerFrame
            frameCount = 0
            startTime = now

            if (logFps) Logger.log("FPS: $fps")
        }

        return fps
    }
}