package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader

class CompressedTextureShader : BaseShader() {

    private var uAlphaTextureHandle: Int = 0

    override fun initShaders() {
        attributes = arrayOf("a_Position", "a_TexCoordinate")

        vertexShader =
                "uniform mat4 u_MVPMatrix; \n" + // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position; \n" + // Per-vertex position information we will pass in.
                "attribute vec2 a_TexCoordinate; \n" + // Per-vertex texture coordinate information we will pass in.
                "varying vec2 v_TexCoordinate; \n" + // This will be passed into the fragment shader.

                "void main() { \n" +
                "	  v_TexCoordinate = a_TexCoordinate; \n" + // Pass through the texture coordinate.
                "    gl_Position = u_MVPMatrix * a_Position; \n" +
                "} \n"


        fragmentShader =
                "precision mediump float; \n" +
                "uniform sampler2D u_Texture; \n" + // The input texture.
                "uniform sampler2D u_AlphaTexture; \n" + // The input alpha texture.
                "uniform float u_MixModifier; \n" +
                "varying vec2 v_TexCoordinate; \n" + // Interpolated texture coordinate per fragment.

                "void main() { \n" +
                "		gl_FragColor = texture2D(u_Texture, v_TexCoordinate); \n" +
                "		gl_FragColor.a = texture2D(u_AlphaTexture, v_TexCoordinate).r; \n" +
                "}"
    }

    override fun setShaderParams(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        setMvpMatrixHandle(mp, sp)
    }

    override fun setVariableHandles() {
        // OpenGL has own texture numeration, you can bind up to 50 textures at once. Here we tell
        // it that texture with index 0 will correspond to uTextureHandle and texture with index 1
        // will correspond to uTexture2Handle
        uTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        GLES20.glUniform1i(uTextureHandle, 0)
        uAlphaTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_AlphaTexture")
        GLES20.glUniform1i(uAlphaTextureHandle, 1)

        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
    }
}