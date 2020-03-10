package com.yalantis.glatademo.advanced

import android.opengl.GLES20
import android.view.MotionEvent
import com.yalantis.glata.animation.AxisMovementAnimation
import com.yalantis.glata.animation.TextureSwitchingAnimation
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.animation.AnimationList
import com.yalantis.glata.core.scene.Scene
import com.yalantis.glata.core.shader.ShaderVariables
import com.yalantis.glata.core.shader.Shaders
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.model.Model
import com.yalantis.glata.model.Rectangle
import com.yalantis.glata.model.SegmentedPlane
import com.yalantis.glata.motion.MotionManager
import com.yalantis.glata.motion.behavior.DraggableObject
import com.yalantis.glata.motion.boundingshape.BoundingRect
import com.yalantis.glata.motion.dragbounds.DragBounds
import com.yalantis.glata.primitive.Align
import com.yalantis.glata.primitive.Axis
import com.yalantis.glata.primitive.Color
import com.yalantis.glata.primitive.Vector3
import com.yalantis.glata.shader.FloatingAlphaBlurShader
import com.yalantis.glata.texture.Texture
import kotlin.math.sin

class AdvancedBackgroundScene(private val textures: Array<ITexture>) : Scene() {

    private val textureIds = IntArray(textures.size)

    private val background = SegmentedPlane(width = 10f, height = 10f, segsW = 12, segsH = 12)
    private val fadingBackground = SegmentedPlane(10f, 10f, 12, 12)
    private val thumbBoundingRect = BoundingRect(1f, 3f, Vector3(-2f, -5f, 0f),
            Align.CENTER_BOTTOM)
    private val thumbPicker = Rectangle.builder().setSize(0.5f, 0.33f).setHasTexture()
            .setPivotAlignment(Align.CENTER_BOTTOM).build()
    private val thumbPointer = Rectangle.builder().setSize(0.075f, 0.3f).setHasTexture()
            .build()

    private val bubbles = Array<Model?>(15) { null }

    private var currentFadingBackgroundAlpha = 0f
    private val thumbStartPos = -2f
    private val thumbEndPos = 2f

    private val motionManager = MotionManager()

    var onSwipeListener: OnSwipeListener? = null

    override fun onAttach(rendererParams: RendererParams) {
        super.onAttach(rendererParams)
        setupScene(rendererParams)
        initBackground(rendererParams)
        addJellyfish(rendererParams)
        initPicker(rendererParams)
        initBubbles(rendererParams)
    }

    override fun onSurfaceChanged(rendererParams: RendererParams, width: Int, height: Int) {
        super.onSurfaceChanged(rendererParams, width, height)
        motionManager.onSurfaceChanged(rendererParams, width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchHappened = motionManager.onTouchEvent(sceneParams, event)
        if (touchHappened) {
            updatePointerPosition()
            onSwipeListener?.let {
                val fullPath = thumbEndPos - thumbStartPos
                val newProgress = (thumbBoundingRect.position.x - thumbStartPos) / fullPath
                it.onSwipe(newProgress)
            }

            if (event.action == MotionEvent.ACTION_UP) onSwipeListener?.onEndSwipe()
        }
        return touchHappened
    }

    private fun initBubbles(rendererParams: RendererParams) {
        val bubbleTexture = rendererParams.managers.textureManager
                .add(Texture(rendererParams, "adv_bubble"))

        for (i in bubbles.indices) {
            val size = (0.17f * (i + 1)) % 0.2f + 0.1f
            val bubble1 = Rectangle.builder().setSize(size, size).setHasTexture().build()
            bubble1.modelParams.apply {
                setShader(Shaders.DEFAULT_TEXTURE_SHADER)
                textureId = bubbleTexture
            }
            bubble1.animation =
                    AxisMovementAnimation(Axis.Y,
                            -5.5f - 1f * i,
                            5.5f + 1f * i,
                            8000f + 1000f * i
                    ).apply { isInfinite = true }
            addChild(bubble1)
            bubbles[i] = bubble1
        }
    }

    private fun animateBubbles() {
        for (i in bubbles.indices) {
            bubbles[i]?.modelParams?.transform?.position?.apply {
                x = i * 3.67f + 1f
                if (x > 4.6f) {
                    x = x % 4.6f
                }
                x -= 2.3f + 0.055f * sin(y * 3f + i / 5f)
            }
        }
    }

    private fun initPicker(rendererParams: RendererParams) {
        val back = Rectangle.builder()
                .setSize(4.55f, 0.2f)
                .setHasTexture()
                .build()
        back.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            setTexture(rendererParams, "adv_scale")
            transform.setPosition(-0.1f, -4.2f)
        }
        addChild(back)

        thumbPicker.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            transform.setPosition(-2f, -5f)
            setTexture(rendererParams, "adv_thumb")
        }
        addChild(thumbPicker)

        val motionable = DraggableObject(thumbPicker,
                thumbBoundingRect,
                DragBounds(-2.5f, 2.391547f, -2f, -5f)
        )
        motionManager.addMotionable(motionable)

        thumbPointer.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            transform.setPosition(Axis.Y, -5f)
            setTexture(rendererParams, "adv_pointer")

        }
        updatePointerPosition()
        addChild(thumbPointer)
    }

    private fun updatePointerPosition() {
        thumbPointer.modelParams.transform.apply {
            position.setFrom(thumbPicker.modelParams.transform.position)
            addToPosition(Axis.Y, 0.8f)
        }
    }

    private fun moveThumbToProgress(progress: Float) {
        val newPos = thumbStartPos + (thumbEndPos - thumbStartPos) * progress
        thumbPicker.modelParams.transform.setPosition(Axis.X, newPos)
        thumbBoundingRect.position.x = newPos
        updatePointerPosition()
    }

    private fun addJellyfish(rendererParams: RendererParams) {
        val texIds = IntArray(9)
        for (i in texIds.indices) {
            texIds[i] = rendererParams.managers.textureManager.add(Texture(rendererParams, "adv_jelly_1_$i"))
        }
        val jelly1 = Rectangle.builder().setSize(3.75f, 2.4f)
                .setTexture(texIds[0])
                .build()
        jelly1.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            transform.setRotation(Axis.Z, -15f)
            blendFuncParams = GLES20.GL_ONE to GLES20.GL_ONE
        }
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)
        jelly1.animation = AnimationList().apply {
            addAnimation(TextureSwitchingAnimation(texIds, 80f))
            addAnimation(AxisMovementAnimation(Axis.X, -4f, 3.5f, 16000f).apply {
                isInfinite = true
            })
            addAnimation(AxisMovementAnimation(Axis.Y, -15f, 12f, 16000f).apply {
                isInfinite = true
            })
        }
        addChild(jelly1)

        val jelly2 = Rectangle.builder().setSize(3.75f, 2.4f)
                .setTexture(texIds[0])
                .build()
        jelly2.modelParams.apply {
            setShader(Shaders.DEFAULT_TEXTURE_SHADER)
            transform.setRotation(Axis.Z, -110f)
            blendFuncParams = GLES20.GL_ONE to GLES20.GL_ONE
        }
        jelly2.animation = AnimationList().apply {
            addAnimation(TextureSwitchingAnimation(texIds, 80f))
            addAnimation(AxisMovementAnimation(Axis.X, -10f, 10f, 13000f).apply {
                isInfinite = true
            })
            addAnimation(AxisMovementAnimation(Axis.Y, 0f, -5f, 13000f).apply {
                isInfinite = true
            })
        }
        addChild(jelly2)
    }

    private fun setupScene(rendererParams: RendererParams) {
        rendererParams.managers.shaderManager.apply {
            add(Shaders.TINTED_TEXTURE_SHADER)
            add(Shaders.BLUR_SHADER)
            add(Shaders.DEFAULT_TEXTURE_SHADER)
            add(Shaders.DEFAULT_COLOR_SHADER)
            add("alphaBlur", FloatingAlphaBlurShader())
        }
        rendererParams.managers.textureManager.apply {
            for (i in textures.indices) {
                textureIds[i] = add(textures[i])
            }
        }
        sceneParams.camera.apply {
            setMaximumVisibleArea(10f, 10f)
            setPivot(Align.CENTER)
            setProjectionMatrix()
        }
    }

    private fun initBackground(rendererParams: RendererParams) {
        background.geometry.isDoubleSideEnabled = true
        background.modelParams.apply {
            setShader(Shaders.BLUR_SHADER)
            textureId = textureIds[0]
        }
        addChild(background)

        fadingBackground.geometry.isDoubleSideEnabled = true
        fadingBackground.modelParams.apply {
            setShader(rendererParams, "alphaBlur")
            textureId = textureIds[0]
            shaderVars = ShaderVariables()
        }
        addChild(fadingBackground)

        val tint = Rectangle.builder()
                .setSize(10f, 10f)
                .setColor(Color(0f, 0f, 0f, 0.75f))
                .build()
        tint.modelParams.apply {
            setShader(Shaders.DEFAULT_COLOR_SHADER)
        }
        addChild(tint)
    }

    override fun onDrawFrame(rendererParams: RendererParams) {
        fadingBackground.modelParams.shaderVars?.alpha = currentFadingBackgroundAlpha
        animateBubbles()
        super.onDrawFrame(rendererParams)
    }

    fun updateScrollAnimation(progress: Float) {
        moveThumbToProgress(progress)
        when {
            progress < 0.35f -> {
                background.modelParams.textureId = textureIds[0]
                fadingBackground.modelParams.textureId = textureIds[1]
                currentFadingBackgroundAlpha = progress / 0.35f
            }
            progress in 0.35f..0.7f -> {
                background.modelParams.textureId = textureIds[1]
                fadingBackground.modelParams.textureId = textureIds[2]
                currentFadingBackgroundAlpha = (progress - 0.35f) / 0.35f
            }
            progress > 0.7f -> {
                background.modelParams.textureId = textureIds[2]
                fadingBackground.modelParams.textureId = textureIds[3]
                currentFadingBackgroundAlpha = (progress - 0.7f) / 0.29f
            }
        }
    }

    interface OnSwipeListener {
        fun onSwipe(newProgress: Float)
        fun onEndSwipe()
    }
}