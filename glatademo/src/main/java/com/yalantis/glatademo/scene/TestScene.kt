package com.yalantis.glatademo.scene

import android.view.animation.AccelerateDecelerateInterpolator
import com.yalantis.glata.animation.AxisMovementAnimation
import com.yalantis.glata.animation.AxisRotationAnimation
import com.yalantis.glata.animation.ScalingAnimation
import com.yalantis.glata.core.FpsCounter
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.Shaders
import com.yalantis.glata.model.Rectangle
import com.yalantis.glata.primitive.Align
import com.yalantis.glata.primitive.Axis
import com.yalantis.glata.primitive.Color
import com.yalantis.glata.texture.Texture

class TestScene(sp: SceneParams = SceneParams()) : Scene(sp) {

    private val fpsCounter = FpsCounter()

    override fun onAttach(rp: RendererParams) {
        super.onAttach(rp)
        rp.managers.shaderManager.add(Shaders.DEFAULT_COLOR_SHADER)
        rp.managers.shaderManager.add(Shaders.DEFAULT_TEXTURE_SHADER)

        createMiddleRow(rp)
        createTopRow(rp)
    }

    override fun onSurfaceChanged(rp: RendererParams, width: Int, height: Int) {
        super.onSurfaceChanged(rp, width, height)
        if (width > height) {
            sceneParams.camera.setVerticalSizeInUnits(3.2f)
        } else {
            sceneParams.camera.setHorizontalSizeInUnits(3.2f)
        }
        sceneParams.camera.setPivot(Align.CENTER)
        sceneParams.camera.setProjectionMatrix()
    }

    override fun onDrawFrame(rp: RendererParams) {
        super.onDrawFrame(rp)
        fpsCounter.countFps()
    }

    private fun createTopRow(rp: RendererParams) {
        val rowHeight = 0.6f
        val rect1 = Rectangle.builder().flipTextureHorizontally().flipTextureVertically().build()
        rect1.modelParams.textureId = rp.managers.textureManager.add(Texture(rp, "ava_2").apply { isRepeating = true })
        rect1.animation = ScalingAnimation(0.8f, 1f, 1000f).apply {
            isInfinite = true
            interpolator = AccelerateDecelerateInterpolator()
        }
        rect1.modelParams.setShader(Shaders.DEFAULT_TEXTURE_SHADER)
        rect1.modelParams.transform.position.x = -1.1f
        rect1.modelParams.transform.position.y = rowHeight
        children.add(rect1)

        val rect2 = Rectangle(1f, 1f, 3f, 3f)
        rect2.animation = AxisMovementAnimation(Axis.Y, rowHeight, rowHeight + 1.1f, 1000f)
                .apply {
                    isInfinite = true
                    returnToInitialAfterFinished = true
                }
        rect2.modelParams.textureId = rect1.modelParams.textureId
        rect2.modelParams.setShader(Shaders.DEFAULT_TEXTURE_SHADER)
        rect2.modelParams.transform.position.y = rowHeight
        children.add(rect2)

        val rect3 = Rectangle.builder().setHasTexture().build()
        rect3.animation = AxisRotationAnimation(90f)
        rect3.modelParams.setShader(Shaders.DEFAULT_TEXTURE_SHADER)
        rect3.modelParams.transform.position.x = 1.1f
        rect3.modelParams.transform.position.y = rowHeight
        children.add(rect3)
    }

    private fun createMiddleRow(rp: RendererParams) {
        val rowHeight = -0.5f

        val rect1 = Rectangle.builder()
                .setGradientColor(Color(1f, 0f, 0f, 1f), Color(0f, 1f, 0f, 1f),
                        Color(0f, 0f, 1f, 1f), Color(0.6f, 0.6f, 0.6f, 1f))
                .build()
        rect1.modelParams.setShader(Shaders.DEFAULT_COLOR_SHADER)
        rect1.animation = ScalingAnimation(0.8f, 1f, 1000f).apply {
            isInfinite = true
            interpolator = AccelerateDecelerateInterpolator()
        }
        rect1.modelParams.transform.position.x = -1.1f
        rect1.modelParams.transform.position.y = rowHeight
        children.add(rect1)

        val rect2 = Rectangle.builder()
                .setVerticalGradientColor(Color(1f, 0f, 0f, 1f), Color(0f, 1f, 0f, 1f))
                .build()
        rect2.modelParams.setShader(Shaders.DEFAULT_COLOR_SHADER)
        rect2.animation = AxisMovementAnimation(Axis.Y, 0f, -1.1f, 1000f)
                .apply {
                    isInfinite = true
                    returnToInitialAfterFinished = true
                }
        rect2.modelParams.transform.position.y = rowHeight
        children.add(rect2)

        val rect3 = Rectangle.builder()
                .setSize(0.85f, 0.85f)
                .setHorizontalGradientColor(Color(1f, 0f, 0f, 1f), Color(0f, 1f, 0f, 1f))
                .build()
        rect3.modelParams.setShader(Shaders.DEFAULT_COLOR_SHADER)
        rect3.animation = AxisRotationAnimation(90f)
        rect3.modelParams.transform.position.x = 1.1f
        rect3.modelParams.transform.position.y = rowHeight
        children.add(rect3)
    }
}