package com.yalantis.glata.core.texture

import com.yalantis.glata.core.RendererParams

interface ITexture {
    fun getName(): String
    fun createTexture(rendererParams: RendererParams)
    fun bind(rendererParams: RendererParams)
    fun unbind()
}