package com.yalantis.glata.core.model

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.scene.SceneParams

/**
 * All magic happens here.
 */
class Geometry {

    companion object {
        // We have arrays of data, e.g. position array in rectangle will have
        // 12 values - 4 points with 3 coordinates each. Here we define how many values has
        // each part of our model.
        const val POSITION_DATA_SIZE_IN_ELEMENTS: Int = 3 // position has 3 values: x, y, z
        const val NORMAL_DATA_SIZE_IN_ELEMENTS: Int = 3 // normal has 3 values: i, j, k
        const val TEXTURE_DATA_SIZE_IN_ELEMENTS: Int = 2 // texture coordinates has 2 values: u, v
        const val COLOR_DATA_SIZE_IN_ELEMENTS: Int = 4 // color has 4 values: r, g, b, a
    }

    // These buffers hold our model. Vertices are point's positions. Normals are direction vectors
    // that are needed to calculate how the object is shadowed. You will need them if you use light.
    // Texture coordinates define how the bitmap is applied to our model. Color buffer holds color
    // of every vertex, by default it's white. You don't need it if you are applying texture to a
    // model. Indices define in what order vertices are grouped to form triangles.
    private var vertexBuffer: VertexBuffer? = null
    private var normalBuffer: VertexBuffer? = null
    private var textureCoordBuffer: VertexBuffer? = null
    private var colorBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null

    /**
     * Any model is just a bunch of triangles. Each triangle is by default drawn from one side, if
     * you flip the triangle it will be invisible. If your model will be rotated and flipped so
     * both sides have to be visible, set this to true.
     */
    var isDoubleSideEnabled = false

    /**
     * If true, models closer to camera will cover models that are far away. If false, model that
     * is drawn later will cover model that is drawn earlier. If set to true, can cause artifacts
     * with planes that have same or nearly same depth coordinate. It can not occure on your device
     * but will occure on someone's else. If you are using 2D graphics, leave this flag as false and
     * just manage your drawing order.
     */
    var isDepthTestingEnabled = false

    /**
     * Switches alpha blending for this model. Drawing a bunch of transparent models covered by two
     * bunches of other transparent models can cause fps issues.
     */
    var isAlphaBlendingEnabled = true

    /**
     * Model-View-Projection Matrix. Each set of position elements will be multiplied by this matrix
     * so OpenGL can calculate how the model will look on the screen.
     * This matrix holds position of the model, position of the camera and projection settings.
     */
    private val MVPMatrix = FloatArray(16)

    /**
     * Every time the app goes to background or the screen is locked, video memory is cleared. Every
     * time this happens, renderer's version is incremented. Every Geometry object and every texture
     * compares it's version with renderer's version and recreates itself if it is needed.
     */
    private var version: Int = -1

    fun createGeometry(vertices: FloatArray,
                       texCoords: FloatArray?,
                       normals: FloatArray?,
                       colors: FloatArray?,
                       indices: ShortArray) {
        vertexBuffer = VertexBuffer(vertices)
        texCoords?.let { textureCoordBuffer = VertexBuffer(texCoords) }
        normals?.let { normalBuffer = VertexBuffer(normals) }
        colors?.let { colorBuffer = VertexBuffer(colors) }
        indexBuffer = IndexBuffer(indices)
    }

    fun prepare(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        if (!mp.isVisible) {
            unbindAllBuffers()
            return
        }

        setGlOption(GLES20.GL_CULL_FACE, !isDoubleSideEnabled)
        setGlOption(GLES20.GL_DEPTH_TEST, isDepthTestingEnabled)

        if (isAlphaBlendingEnabled) {
            setGlOption(GLES20.GL_BLEND, true)
            applyBlendFunc(mp.blendFuncParams ?: rp.blendFuncParams)
        }

        rp.managers.shaderManager.use(rp, mp.shaderId)
    }

    fun draw(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        if (!mp.isVisible) {
            unbindAllBuffers()
            return
        }

        if (rp.isVboEnabled) {
            drawVirtualBuffers(rp, mp, sp)
        } else {
            drawBuffers(rp, mp, sp)
        }
    }

    private fun drawVirtualBuffers(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        if (version != rp.version) initBufferObject(rp)

        val shader = rp.managers.shaderManager.get(mp.shaderId)

        shader ?: throw RuntimeException("Shader ${mp.shaderId} for ${mp.name} not found")

        shader.setShaderParams(rp, mp, sp)

        vertexBuffer?.let {
            it.bind()
            GLES20.glVertexAttribPointer(shader.aPositionHandle, POSITION_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0)
            GLES20.glEnableVertexAttribArray(shader.aPositionHandle)
        } ?: throw RuntimeException("Vertex buffer is null")

        if (shader.aNormalHandle > 0) {
            normalBuffer?.let { normalBuffer ->
                normalBuffer.bind()
                GLES20.glVertexAttribPointer(shader.aNormalHandle, NORMAL_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0)
                GLES20.glEnableVertexAttribArray(shader.aNormalHandle)
            } ?: throw RuntimeException("Normal  buffer is null")
        }

        if (shader.aTexCoordinateHandle > 0) {
            textureCoordBuffer?.let { textureCoordBuffer ->
                textureCoordBuffer.bind()
                GLES20.glVertexAttribPointer(shader.aTexCoordinateHandle, TEXTURE_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0)
                GLES20.glEnableVertexAttribArray(shader.aTexCoordinateHandle)
                rp.managers.textureManager.bind(rp, mp.textureId)
            } ?: throw RuntimeException("Texture coord  buffer is null")
        } else {
            rp.managers.textureManager.unbindCurrentTexture()
        }

        if (shader.aColorHandle > 0) {
            colorBuffer?.let { colorBuffer ->
                colorBuffer.bind()
                GLES20.glVertexAttribPointer(shader.aColorHandle, COLOR_DATA_SIZE_IN_ELEMENTS, GLES20.GL_FLOAT, false, 0, 0)
                GLES20.glEnableVertexAttribArray(shader.aColorHandle)
            } ?: throw RuntimeException("Color buffer is null")
        }

        indexBuffer?.let { indexBuffer ->
            indexBuffer.bind()
            GLES20.glDrawElements(mp.renderMode, indexBuffer.size, GLES20.GL_UNSIGNED_SHORT, 0)
        } ?: throw RuntimeException("Index buffer is null")

        unbindAllBuffers()
    }

    private fun drawBuffers(rp: RendererParams, mp: ModelParams, sp: SceneParams) {
        val shader = rp.managers.shaderManager.get(mp.shaderId)
        shader ?: throw RuntimeException("Shader ${mp.shaderId} for ${mp.name} not found")
        shader.setShaderParams(rp, mp, sp)

        vertexBuffer?.let { vertexBuffer ->
            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(shader.aPositionHandle)
            GLES20.glVertexAttribPointer(
                    shader.aPositionHandle,
                    POSITION_DATA_SIZE_IN_ELEMENTS,
                    GLES20.GL_FLOAT,
                    false,
                    POSITION_DATA_SIZE_IN_ELEMENTS * vertexBuffer.bytesPerElement,
                    vertexBuffer.buffer)
        } ?: throw RuntimeException("Vertex buffer is null")

        if (shader.aNormalHandle > 0) {
            normalBuffer?.let { normalBuffer ->
                normalBuffer.position(0)
                GLES20.glEnableVertexAttribArray(shader.aNormalHandle)
                GLES20.glVertexAttribPointer(
                        shader.aNormalHandle,
                        NORMAL_DATA_SIZE_IN_ELEMENTS,
                        GLES20.GL_FLOAT,
                        false,
                        NORMAL_DATA_SIZE_IN_ELEMENTS * normalBuffer.bytesPerElement,
                        normalBuffer.buffer)

            } ?: throw RuntimeException("Normal buffer is null")
        }

        if (shader.aTexCoordinateHandle > 0) {
            textureCoordBuffer?.let { textureCoordBuffer ->
                textureCoordBuffer.position(0)
                GLES20.glEnableVertexAttribArray(shader.aTexCoordinateHandle)
                GLES20.glVertexAttribPointer(
                        shader.aTexCoordinateHandle,
                        TEXTURE_DATA_SIZE_IN_ELEMENTS,
                        GLES20.GL_FLOAT,
                        false,
                        TEXTURE_DATA_SIZE_IN_ELEMENTS * textureCoordBuffer.bytesPerElement,
                        textureCoordBuffer.buffer)
                rp.managers.textureManager.bind(rp, mp.textureId)
            } ?: throw RuntimeException("TExture coord buffer is null")
        } else {
            rp.managers.textureManager.unbindCurrentTexture()
        }

        if (shader.aColorHandle > 0) {
            colorBuffer?.let { colorBuffer ->
                colorBuffer.position(0)
                GLES20.glEnableVertexAttribArray(shader.aColorHandle)
                GLES20.glVertexAttribPointer(
                        shader.aColorHandle,
                        COLOR_DATA_SIZE_IN_ELEMENTS,
                        GLES20.GL_FLOAT,
                        false,
                        COLOR_DATA_SIZE_IN_ELEMENTS * colorBuffer.bytesPerElement,
                        colorBuffer.buffer)

            } ?: throw RuntimeException("Color buffer is null")

        }

        indexBuffer?.let { indexBuffer ->
            indexBuffer.position(0)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer.buffer)
        } ?: throw RuntimeException("Index buffer is null")

        unbindAllBuffers()
    }

    private fun applyBlendFunc(blendFuncParams: Pair<Int, Int>) {
        GLES20.glBlendFunc(blendFuncParams.first, blendFuncParams.second)
    }

    private fun setGlOption(optionId: Int, isEnabled: Boolean) {
        if (isEnabled) {
            GLES20.glEnable(optionId)
        } else {
            GLES20.glDisable(optionId)
        }
    }

    fun putAllFromInnerArrays() {
        vertexBuffer?.putFromInnerArray()
        textureCoordBuffer?.putFromInnerArray()
        normalBuffer?.putFromInnerArray()
        colorBuffer?.putFromInnerArray()
        indexBuffer?.putFromInnerArray()
    }

    fun allocateAll() {
        vertexBuffer?.allocate()
        textureCoordBuffer?.allocate()
        normalBuffer?.allocate()
        colorBuffer?.allocate()
        indexBuffer?.allocate()
    }

    fun initBufferObject(rp: RendererParams) {
        vertexBuffer?.initBuffer()
        textureCoordBuffer?.initBuffer()
        normalBuffer?.initBuffer()
        colorBuffer?.initBuffer()
        indexBuffer?.initBuffer()

        version = rp.version
    }

    fun deleteArrays() {
        vertexBuffer?.deleteArray()
        textureCoordBuffer?.deleteArray()
        normalBuffer?.deleteArray()
        colorBuffer?.deleteArray()
        indexBuffer?.deleteArray()
    }

    private fun unbindAllBuffers() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}