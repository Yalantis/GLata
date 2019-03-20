package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader

class DefaultTextureShader : BaseShader() {

    override fun initShaders() {
        attributes = arrayOf("a_Position", "a_TexCoordinate")

        vertexShader =
                "uniform mat4 u_MVPMatrix; \n" + // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position; \n" + // Per-vertex position information we will pass in.
                "attribute vec2 a_TexCoordinate; \n" + // Per-vertex color information we will pass in.
                "varying vec2 v_TexCoordinate; \n" + // This will be passed into the fragment shader.

                "void main() { \n" + // The entry point for our vertex shader.
                "   v_TexCoordinate = a_TexCoordinate; \n" + // Pass the color of the current vertex to the fragment shader
                // gl_Position is a special variable used to store the final position.
                // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
                "   gl_Position = u_MVPMatrix * a_Position; \n" +
                "}"

        fragmentShader =
                // Set the default precision to medium. We don't need high precision in this shader.
                "precision mediump float; \n" +
                "uniform sampler2D u_Texture; \n" + // The input texture.
                "varying vec2 v_TexCoordinate; \n" + // This is the color from the vertex shader interpolated across the triangle per fragment

                "void main() {\n" + // The entry point for our fragment shader.
                // texture coordinates are interpolated for every fragment of the model and specific
                // part of the texture is applied according to texture coordinates
                "   gl_FragColor = texture2D(u_Texture, v_TexCoordinate);\n" +
                "}"
    }

    override fun setShaderParams(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        setMvpMatrixHandle(mp, sp)
    }

    override fun setVariableHandles() {
        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        uTextureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aTexCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate")
    }
}