package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader

class TintedTextureShader : BaseShader() {

    override fun initShaders() {
        attributes = arrayOf("a_Position", "a_TexCoordinate", "a_Color")

        vertexShader =
                "uniform mat4 u_MVPMatrix; \n" +
                        "attribute vec4 a_Position; \n" +
                        "attribute vec2 a_TexCoordinate; \n" +
                        "attribute vec4 a_Color; \n" +
                        "varying vec2 v_TexCoordinate; \n" +
                        "varying vec4 v_Color; \n" +

                        "void main() { \n" +
                        "   v_TexCoordinate = a_TexCoordinate; \n" +
                        "   v_Color = a_Color; \n" +
                        "   gl_Position = u_MVPMatrix * a_Position; \n" +
                        "}"

        fragmentShader =
                "precision mediump float; \n" +
                        "uniform sampler2D u_Texture; \n" +
                        "varying vec2 v_TexCoordinate; \n" +
                        "varying vec4 v_Color; \n" +

                        "void main() { \n" +
                        "   gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);\n" +
                        "}"
    }

    override fun setShaderParams(
            rendererParams: RendererParams, modelParams: ModelParams, sceneParams: SceneParams) {
        setMvpMatrixHandle(modelParams, sceneParams)
    }

    override fun setVariableHandles() {
        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        uTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color")
        aTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
    }
}