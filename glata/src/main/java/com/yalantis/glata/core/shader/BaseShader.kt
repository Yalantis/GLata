package com.yalantis.glata.core.shader

import android.opengl.GLES20
import android.opengl.Matrix
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.model.ModelParams
import com.yalantis.glata.core.scene.SceneParams
import com.yalantis.glata.util.Logger

/**
 * This is base shader program. It consists of two shaders: vertex and fragment.
 * Vertex shader is executed for every vertex of your model in scene space and calculates mostly geometry.
 * Fragment shader is executed for different parts of your model in screen space and calculates
 * the appearance of it on the screen.
 * You need to extend this class if you will be creating your own shader.
 */
abstract class BaseShader {

    val name: String = this.javaClass.name

    protected lateinit var attributes: Array<String>
    protected lateinit var vertexShader: String
    protected lateinit var fragmentShader: String

    // These variables hold IDs in video memory of the shaders and shader program
    var programHandle: Int = 0
    private var vertexShaderHandle: Int = 0
    private var fragmentShaderHandle: Int = 0

    // These variables hold IDs in video memory of main attributes of the model
    var aPositionHandle: Int = 0
        protected set
    var aNormalHandle: Int = 0
        protected set
    var aTexCoordinateHandle: Int = 0
        protected set
    var aColorHandle: Int = 0
        protected set

    var uMvpMatrixHandle: Int = 0 // Id of the model-view-projection matrix uniform
        protected set

    var uTextureHandle: Int = 0 // Id of the texture
        protected set

    var mvpMatrix = FloatArray(16)

    private var isInitialized: Boolean = false
    private var isCompiled: Boolean = false
    private var version: Int = -1

    private fun initShaderCode() {
        if (!isInitialized) {
            initShaders()
            isInitialized = true
        }
    }

    protected abstract fun initShaders()

    abstract fun setShaderParams(
            rendererParams: RendererParams, modelParams: ModelParams, sceneParams: SceneParams)

    protected abstract fun setVariableHandles()

    fun use(rendererParams: RendererParams) {
        compile(rendererParams)
        GLES20.glUseProgram(programHandle)
        setVariableHandles()
    }

    fun compile(rendererParams: RendererParams) {
        if (version == rendererParams.version) return

        initShaderCode()

        vertexShaderHandle = compileSingleShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        fragmentShaderHandle = compileSingleShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        programHandle = createAndLinkProgram()
        version = rendererParams.version
    }

    private fun compileSingleShader(shaderType: Int, shaderCode: String) : Int {
        var shaderHandle = GLES20.glCreateShader(shaderType)
        var compileError: String? = null

        if (shaderHandle != 0) {
            // Pass in the shader source
            GLES20.glShaderSource(shaderHandle, shaderCode)

            // Compile the shader
            GLES20.glCompileShader(shaderHandle)

            // Get the compilation status
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            // If the compilation failed, delete the shader
            if (compileStatus[0] == 0) {
                compileError = GLES20.glGetShaderInfoLog(shaderHandle)
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }

        if (shaderHandle == 0) {
            isCompiled = false
            throw RuntimeException("Error creating shader $name: $compileError")
        } else {
            isCompiled = true
        }

        return shaderHandle
    }

    private fun createAndLinkProgram() : Int {
        var programHandle = GLES20.glCreateProgram()

        if (programHandle != 0) {
            // Bind the vertex shader to the program
            GLES20.glAttachShader(programHandle, vertexShaderHandle)

            // Bind the fragment shader to the program
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)

            // Bind attributes
            for (i in attributes.indices) {
                GLES20.glBindAttribLocation(programHandle, i, attributes[i])
            }

            // Link the two shaders together into a program
            GLES20.glLinkProgram(programHandle)

            // Get the link status
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program
            if (linkStatus[0] == 0) {
                Logger.log("Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle))
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }

        if (programHandle == 0) {
            throw RuntimeException("Error creating shader program.")
        }

        return programHandle
    }

    protected fun setMvpMatrixHandle(modelParams: ModelParams, sceneParams: SceneParams) {
        Matrix.multiplyMM(mvpMatrix, 0, sceneParams.camera.viewMatrix, 0, modelParams.transform.getMatrix(), 0)
        Matrix.multiplyMM(mvpMatrix, 0, sceneParams.camera.projectionMatrix, 0, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMvpMatrixHandle, 1, false, mvpMatrix, 0)
    }
}