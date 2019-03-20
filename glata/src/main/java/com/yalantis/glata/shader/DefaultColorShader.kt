package com.yalantis.glata.shader

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.core.shader.BaseShader

class DefaultColorShader : BaseShader() {

    override fun initShaders() {
        attributes = arrayOf("a_Position", "a_Color")

        vertexShader =
            "uniform mat4 u_MVPMatrix; \n" + // A constant representing the combined model/view/projection matrix.
            "attribute vec4 a_Position; \n" + // Per-vertex position information we will pass in.
            "attribute vec4 a_Color; \n" + // Per-vertex color information we will pass in.
            "varying vec4 v_Color; \n" + // This will be passed into the fragment shader.

            "void main() { \n" + // The entry point for our vertex shader.
            "   v_Color = a_Color; \n" + // Pass the color of the current vertex to the fragment shader
            // gl_Position is a special variable used to store the final position.
            // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
            "   gl_Position = u_MVPMatrix * a_Position; \n" +
            "}"

        fragmentShader =
            // Set the default precision to medium. We don't need high precision in this shader.
            "precision mediump float; \n" +
            "varying vec4 v_Color; \n" + // This is the color from the vertex shader interpolated across the triangle per fragment

            "void main() {\n" + // The entry point for our fragment shader.
            "   gl_FragColor = v_Color;\n" +//v_Color;\n" + // Pass the color directly through the pipeline.
            "}"
    }

    override fun setShaderParams(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        setMvpMatrixHandle(mp, sp)
    }

    override fun setVariableHandles() {
        uMvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        aPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        aColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color")
    }
}