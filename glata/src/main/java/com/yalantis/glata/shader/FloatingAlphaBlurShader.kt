package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader
import com.yalantis.glata.util.Utils

class FloatingAlphaBlurShader(val sourceAlpha: SourceAlpha = SourceAlpha.SRC_ALPHA,
                            val destination: Destination = Destination.ONLY_ALPHA) : BaseShader() {

    private var uAlphaHandle: Int = 0

    override fun initShaders() {
        attributes = arrayOf("a_Position", "a_TexCoordinate")//, "a_Color")

        vertexShader =
                "uniform mat4 u_MVPMatrix; \n" +
                        "uniform sampler2D u_Texture; \n" +
                        "uniform float u_Alpha; \n" +
                        "attribute vec4 a_Position; \n" +
                        "attribute vec2 a_TexCoordinate; \n" +
                        "varying vec4 v_TextureColor; \n" +

                        "void main() { \n" +
                        "   v_TextureColor = texture2D(u_Texture, a_TexCoordinate); \n" +
                        "   ${destination.value} *= ${sourceAlpha.value}; \n" +
                        "   gl_Position = u_MVPMatrix * a_Position; \n" +
                        "}"

        fragmentShader =
                "precision mediump float; \n" +
                        "varying vec4 v_TextureColor; \n" +

                        "void main() { \n" +
                        "   gl_FragColor = v_TextureColor;\n" +
                        "}"
    }

    override fun setShaderParams(
            rendererParams: RendererParams, modelParams: ModelParams, sceneParams: SceneParams) {
        setMvpMatrixHandle(modelParams, sceneParams)
        modelParams.shaderVars?.let { setAlpha(it.alpha) }
    }

    fun setAlpha(alpha: Float) {
        GLES20.glUniform1f(uAlphaHandle, Utils.clamp(0f, 1f, alpha))
    }

    override fun setVariableHandles() {
        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        uAlphaHandle = GLES20.glGetUniformLocation(programHandle, "u_Alpha")
        uTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
    }

    enum class SourceAlpha(val value: String) {
        SRC_ALPHA ("u_Alpha"),
        ONE_MINUS_SRC_ALPHA ("1.0 - u_Alpha")
    }

    enum class Destination(val value: String) {
        ONLY_ALPHA ("v_TextureColor.a"),
        FULL_COLOR ("v_TextureColor")
    }
}