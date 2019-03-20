package com.yalantis.glata.core.model

import android.opengl.GLES20
import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.core.shader.ShaderVariables
import com.yalantis.glata.core.shader.Shaders
import com.yalantis.glata.primitive.Transform
import com.yalantis.glata.texture.Texture

class ModelParams {

    /**
     * Name of the model.
     */
    var name: String = ""

    /**
     * ID of the texture that will be mapped on this model.
     */
    var textureId: Int = -1

    /**
     * ID of the shader that will be used to draw this model.
     */
    var shaderId: Int = -1

    /**
     * Defines is the model visible on the scene. If set to false, model will not be drawn.
     */
    var isVisible: Boolean = true

    /**
     * Defines how the model will be drawn. You can use GL_TRIANGLES, GL_LINES, GL_LINE_STRIP and
     * GL_LINE_LOOP.
     */
    var renderMode: Int = GLES20.GL_TRIANGLES

    /**
     * These are the personal parameters of the model for alpha blending equation. They will be
     * applied while rendering. If this variable in null, then default blending params will be
     * applied. Default blending params can be changed in RendererParams object attached to the
     * renderer.
     * Most common pairs are:
     * (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA) and
     * (GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
     */
    var blendFuncParams: Pair<Int, Int>? = null

    /**
     * Transform of the model. It contains position, rotation and scale of the model.
     */
    val transform = Transform()

    /**
     * This model stores specific variables that are needed for some shader. Default shaders don't
     * need this.
     */
    var shaderVars: ShaderVariables? = null

    fun setShader(shader: Shaders) {
        shaderId = shader.ordinal
    }

    fun setShader(rp: RendererParams, shaderName: String) {
        shaderId = rp.managers.shaderManager.getId(shaderName)
    }

    fun setShaderFrom(another: IModel) {
        shaderId = another.mp.shaderId
    }

    fun setTexture(rp: RendererParams, textureName: String) {
        textureId = rp.managers.textureManager.add(Texture(rp, textureName))
    }

    fun setTexture(rp: RendererParams, resourceId: Int) {
        textureId = rp.managers.textureManager.add(Texture(rp, resourceId))
    }

    fun setTextureFrom(another: IModel) {
        textureId = another.mp.textureId
    }
}