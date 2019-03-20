package com.yalantis.glata.core.shader

import com.yalantis.glata.core.RendererParams
import com.yalantis.glata.util.Logger

class ShaderManager {

    var currentShaderId: Int = -1
        private set

    private val shaders = HashMap<Int, BaseShader>()
    private val shaderNames = HashMap<String, Int>()
    private var nextNamedId: Int = 1000

    fun add(shader: Shaders): Int {
        val shaderId = shader.ordinal

        if (shaders.containsKey(shaderId)) return shaderId

        shaders[shaderId] = shader.createShader()
        return shaderId
    }

    fun add(shaderName: String, shader: BaseShader): Int {

        if (shaderNames.containsKey(shaderName)) return shaderNames[shaderName] ?: -1

        val id = nextNamedId
        nextNamedId++

        shaderNames[shaderName] = id
        shaders[id] = shader

        return id
    }

    fun get(shaderId: Int): BaseShader? = shaders[shaderId]

    fun get(shader: Shaders): BaseShader? = shaders[shader.ordinal]

    fun get(shaderName: String): BaseShader? = shaders[shaderNames[shaderName]]

    fun getId(shaderName: String): Int = shaderNames[shaderName] ?: -1

    fun resetCurrentShaderId() {
        currentShaderId = -1
    }

    fun compileShaders(rendererParams: RendererParams) {
        for (shader: BaseShader in shaders.values) {
            shader.compile(rendererParams)
        }
    }

    fun use(rendererParams: RendererParams, shaderId: Int) {
        if (!shaders.containsKey(shaderId)) {
            Logger().log("Shader $shaderId not found")
        }

        //if (shaderId == currentShaderId) return

        shaders[shaderId]?.use(rendererParams)
    }

    fun use(rendererParams: RendererParams, shader: Shaders) {
        use(rendererParams, shader.ordinal)
    }

    fun use(rendererParams: RendererParams, shaderName: String) {
        use(rendererParams, shaderNames[shaderName] ?: -1)
    }
}