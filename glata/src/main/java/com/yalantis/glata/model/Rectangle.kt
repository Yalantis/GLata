package com.yalantis.glata.model

import com.yalantis.glata.primitive.Color

class Rectangle(width: Float,
                height: Float,
                texW: Float,
                texH: Float,
                color: Color?,
                hasNormals: Boolean,
                horizontal: Boolean) : Model() {

    companion object {
        fun builder() = RectangleBuilder()
    }

    var width: Float = -1f
        protected set
    var height: Float = -1f
        protected set

    init {
        this.width = width
        this.height = height

        geometry.createGeometry(
                RectangleBuilder.buildVertexArray(width, height, horizontal),
                RectangleBuilder.buildTexCoordArray(texW, texH),
                RectangleBuilder.buildNormalArray(hasNormals, horizontal),
                RectangleBuilder.buildColorArray(color),
                RectangleBuilder.buildIndexArray())
        geometry.allocateAll()
        geometry.putAllFromInnerArrays()
    }

     private constructor (vertexArray: FloatArray,
        texCoordArray: FloatArray?,
        normalArray: FloatArray?,
        colorArray: FloatArray?,
        indexArray: ShortArray) : this() {
        geometry.createGeometry(vertexArray, texCoordArray, normalArray, colorArray, indexArray)
        geometry.allocateAll()
        geometry.putAllFromInnerArrays()
    }

    constructor() : this(1f, 1f, 1f, 1f, null, false, true)

    constructor(width: Float,
                height: Float,
                texW: Float,
                texH: Float) : this(width, height, texW, texH, null, false, true)

    constructor(width: Float,
                height: Float,
                color: Color) : this(width, height, 0f, 0f, color, false, true)

    class RectangleBuilder {

        companion object {
            fun buildIndexArray(): ShortArray {
                return shortArrayOf(
                        1, 2, 3, 1, 3, 0
                )
            }

            fun buildVertexArray(width: Float, height: Float, horizontal: Boolean): FloatArray {
                val w2 = width * .5f
                val h2 = height * .5f
                return if (horizontal) {
                    floatArrayOf(
                            -w2, h2, 0f,  // 0, Top Left
                            -w2, -h2, 0f,  // 1, Bottom Left
                            w2, -h2, 0f,  // 2, Bottom Right
                            w2, h2, 0f   // 3, Top Right
                    )
                } else {
                    floatArrayOf(
                            -w2, 0f, h2,  // 0, Top Left
                            -w2, 0f, -h2,  // 1, Bottom Left
                            w2, 0f, -h2,  // 2, Bottom Right
                            w2, 0f, h2   // 3, Top Right
                    )
                }
            }

            fun buildTexCoordArray(texW: Float, texH: Float, flipVertically: Boolean = false, flipHorizontally: Boolean = false): FloatArray? {
                if (texW <= 0f || texH <= 0f) return null

                return when {
                    !flipVertically && !flipHorizontally -> {
                        floatArrayOf(
                                0.0f * texW, 0.0f * texH, // 0, Top Left
                                0.0f * texW, 1.0f * texH, // 1, Bottom Left
                                1.0f * texW, 1.0f * texH, // 2, Bottom Right
                                1.0f * texW, 0.0f * texH  // 3, Top Right
                        )
                    }
                    flipVertically && !flipHorizontally -> {
                        floatArrayOf(
                                0.0f * texW, 1.0f * texH, // 0, Top Left
                                0.0f * texW, 0.0f * texH, // 1, Bottom Left
                                1.0f * texW, 0.0f * texH, // 2, Bottom Right
                                1.0f * texW, 1.0f * texH  // 3, Top Right
                        )
                    }
                    !flipVertically && flipHorizontally -> {
                        floatArrayOf(
                                1.0f * texW, 0.0f * texH, // 0, Top Left
                                1.0f * texW, 1.0f * texH, // 1, Bottom Left
                                0.0f * texW, 1.0f * texH, // 2, Bottom Right
                                0.0f * texW, 0.0f * texH  // 3, Top Right
                        )
                    }
                    else -> {
                        floatArrayOf(
                                1.0f * texW, 1.0f * texH, // 0, Top Left
                                1.0f * texW, 0.0f * texH, // 1, Bottom Left
                                0.0f * texW, 0.0f * texH, // 2, Bottom Right
                                0.0f * texW, 1.0f * texH  // 3, Top Right
                        )
                    }
                }
            }

            fun buildColorArray(color: Color?): FloatArray? {
                return if (color != null) {
                    floatArrayOf(
                            color.r, color.g, color.b, color.a,
                            color.r, color.g, color.b, color.a,
                            color.r, color.g, color.b, color.a,
                            color.r, color.g, color.b, color.a
                    )
                } else {
                    null
                }
            }

            fun buildNormalArray(hasNormals: Boolean, horizontal: Boolean): FloatArray? {
                if (!hasNormals) return null

                return if (horizontal) {
                    floatArrayOf(
                            0f, 1f, 0f,
                            0f, 1f, 0f,
                            0f, 1f, 0f,
                            0f, 1f, 0f
                    )
                } else {
                    floatArrayOf(
                            0f, 0f, 1f,
                            0f, 0f, 1f,
                            0f, 0f, 1f,
                            0f, 0f, 1f
                    )
                }
            }
        }

        private var hasColors = false
        private var hasTexture = false
        private var hasNormals = false
        private var isVertical = false

        private var width = 1f
        private var height = 1f

        private var textureRepeatsHorizontal = 1f
        private var textureRepeatsVertical = 1f
        private var isTextureFlippedHorizontally = false
        private var isTextureFlippedVertically = false

        private var colorArray: FloatArray? = null
        private var textureId: Int = -1

        fun setSize(width: Float = 1f, height: Float = 1f): RectangleBuilder {
            this.width = width
            this.height = height
            return this
        }

        fun setTextureRepeats(horizontalMultiplier: Float, verticalMultiplier: Float): RectangleBuilder {
            hasTexture = true
            this.textureRepeatsHorizontal = horizontalMultiplier
            this.textureRepeatsVertical = verticalMultiplier
            return this
        }

        fun setHasNormals(hasNormals: Boolean = true): RectangleBuilder {
            this.hasNormals = hasNormals
            return this
        }

        fun setHasTexture(hasTexture: Boolean = true): RectangleBuilder {
            this.hasTexture = hasTexture
            return this
        }

        fun setTexture(textureId: Int): RectangleBuilder {
            hasTexture = true
            this.textureId = textureId
            return this
        }

        fun flipTextureHorizontally(flipHorizontally: Boolean = true): RectangleBuilder {
            hasTexture = true
            isTextureFlippedHorizontally = flipHorizontally
            return this
        }

        fun flipTextureVertically(flipVertically: Boolean = true): RectangleBuilder {
            hasTexture = true
            isTextureFlippedVertically = flipVertically
            return this
        }

        fun setVerticalGeometry(isVertical: Boolean = true): RectangleBuilder {
            this.isVertical = isVertical
            return this
        }

        fun setColor(color: Color): RectangleBuilder {
            hasColors = true
            colorArray = buildColorArray(color)
            return this
        }

        fun setHorizontalGradientColor(leftColor: Color, rightColor: Color): RectangleBuilder {
            colorArray = floatArrayOf(
                    leftColor.r, leftColor.g, leftColor.b, leftColor.a, // 0, Top Left
                    leftColor.r, leftColor.g, leftColor.b, leftColor.a, // 1, Bottom Left
                    rightColor.r, rightColor.g, rightColor.b, rightColor.a, // 2, Bottom Right
                    rightColor.r, rightColor.g, rightColor.b, rightColor.a  // 3, Top Right
            )
            return this
        }

        fun setVerticalGradientColor(topColor: Color, bottomColor: Color): RectangleBuilder {
            colorArray = floatArrayOf(
                    topColor.r, topColor.g, topColor.b, topColor.a, // 0, Top Left
                    bottomColor.r, bottomColor.g, bottomColor.b, bottomColor.a, // 1, Bottom Left
                    bottomColor.r, bottomColor.g, bottomColor.b, bottomColor.a, // 2, Bottom Right
                    topColor.r, topColor.g, topColor.b, topColor.a  // 3, Top Right
            )
            return this
        }

        fun setGradientColor(topLeftColor: Color, topRightColor: Color, bottomLeftColor: Color, bottomRightColor: Color): RectangleBuilder {
            colorArray = floatArrayOf(
                    topLeftColor.r, topLeftColor.g, topLeftColor.b, topLeftColor.a, // 0, Top Left
                    bottomLeftColor.r, bottomLeftColor.g, bottomLeftColor.b, bottomLeftColor.a, // 1, Bottom Left
                    bottomRightColor.r, bottomRightColor.g, bottomRightColor.b, bottomRightColor.a, // 2, Bottom Right
                    topRightColor.r, topRightColor.g, topRightColor.b, topRightColor.a  // 3, Top Right
            )
            return this
        }

        fun build(): Rectangle {
            val rect = buildAndLeaveArrays()
            rect.geometry.deleteArrays()
            return rect
        }

        fun buildAndLeaveArrays() : Rectangle {
            val vertexArray = buildVertexArray(width, height, !isVertical)

            val texCoordArray = buildTexCoordArray(textureRepeatsHorizontal, textureRepeatsVertical, isTextureFlippedVertically, isTextureFlippedHorizontally)

            val normalArray = buildNormalArray(hasNormals, !isVertical)

            val indexArray = buildIndexArray()

            val rect = Rectangle(vertexArray, texCoordArray, normalArray, colorArray, indexArray)
            if (textureId != -1) {
                rect.mp.textureId = textureId
            }
            rect.width = width
            rect.height = height
            return rect
        }
    }
}