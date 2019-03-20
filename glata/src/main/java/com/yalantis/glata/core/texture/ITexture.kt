package com.yalantis.glata.core.texture

import com.yalantis.glata.core.RendererParams

interface ITexture {
    fun createTexture(rp: RendererParams)
    fun bind(rp: RendererParams)
    fun unbind()
}