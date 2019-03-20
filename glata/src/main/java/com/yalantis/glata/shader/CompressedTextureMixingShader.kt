package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader

class CompressedTextureMixingShader(
        private val alphaHandling: AlphaHandling = AlphaHandling.TWO_INDIVIDUAL_ALPHA_TEXTURES) : BaseShader() {

    private var uTexture2Handle: Int = 0
    private var uAlphaTextureHandle: Int = 0
    private var uAlphaTexture2Handle: Int = 0
    private var uMixModifierHandle: Int = 0

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
                "uniform sampler2D u_Texture2; \n" + // The input texture.
                "uniform sampler2D u_AlphaTexture; \n" // The alpha mask

        if (alphaHandling == AlphaHandling.TWO_INDIVIDUAL_ALPHA_TEXTURES) {
            fragmentShader +=
                    "uniform sampler2D u_AlphaTexture2; \n" // The second alpha mask
        }

        fragmentShader +=
                "uniform float u_MixModifier; \n" +
                "varying vec2 v_TexCoordinate; \n" + // Interpolated texture coordinate per fragment.

                "void main() { \n" +
                alphaHandling.code +
                "}"
    }

    override fun setShaderParams(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        setMvpMatrixHandle(mp, sp)
    }

    fun setTextureMixAmount(mixAmount: Float) {
        GLES20.glUniform1f(uMixModifierHandle, mixAmount)
    }

    override fun setVariableHandles() {
        // OpenGL has own texture numeration, you can bind up to 50 textures at once. Here we tell
        // it that texture with index 0 will correspond to uTextureHandle and texture with index 1
        // will correspond to uTexture2Handle and so on
        uTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        GLES20.glUniform1i(uTextureHandle, 0)
        uTexture2Handle = GLES20.glGetUniformLocation(programHandle, "u_Texture2")
        GLES20.glUniform1i(uTexture2Handle, 1)
        uAlphaTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_AlphaTexture")
        GLES20.glUniform1i(uAlphaTextureHandle, 2)

        if (alphaHandling == AlphaHandling.TWO_INDIVIDUAL_ALPHA_TEXTURES) {
            uAlphaTexture2Handle = GLES20.glGetUniformLocation(programHandle, "u_AlphaTexture2")
            GLES20.glUniform1i(uAlphaTexture2Handle, 3)
        }

        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        uMixModifierHandle = GLES20.glGetUniformLocation(programHandle, "u_MixModifier")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
    }

    enum class AlphaHandling(val code: String) {
        SHARE_SINGLE_ALPHA_TEXTURE (
                "float alpha = texture2D(u_AlphaTexture, v_TexCoordinate).r; \n" +
                        "vec4 col1 = texture2D(u_Texture, v_TexCoordinate); \n" +
                        "col1.a = alpha; \n" +
                        "vec4 col2 = texture2D(u_Texture2, v_TexCoordinate); \n" +
                        "col2.a = alpha; \n" +
                        "gl_FragColor = mix(col1, col2, u_MixModifier); \n"
                //"gl_FragColor.a = texture2D(u_AlphaTexture, v_TexCoordinate).r; \n"
        ),
        TWO_INDIVIDUAL_ALPHA_TEXTURES (
                "vec4 col1 = texture2D(u_Texture, v_TexCoordinate); \n" +
                        "col1.a = texture2D(u_AlphaTexture, v_TexCoordinate).r; \n" +
                        "vec4 col2 = texture2D(u_Texture2, v_TexCoordinate); \n" +
                        "col2.a = texture2D(u_AlphaTexture2, v_TexCoordinate).r; \n" +
                        "gl_FragColor = mix(col1, col2, u_MixModifier); \n"
        )
    }
}