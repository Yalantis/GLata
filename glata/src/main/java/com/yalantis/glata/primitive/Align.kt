package com.yalantis.glata.primitive

enum class Align {
    CENTER {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getTopMargin(verticalSize: Float): Float = verticalSize / 2f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize / 2f
    },
    CENTER_TOP {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getTopMargin(verticalSize: Float): Float = 0f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize
    },
    CENTER_BOTTOM {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize / 2f
        override fun getTopMargin(verticalSize: Float): Float = verticalSize
        override fun getBottomMargin(verticalSize: Float): Float = 0f
    },
    CENTER_LEFT {
        override fun getLeftMargin(horizontalSize: Float): Float = 0f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize
        override fun getTopMargin(verticalSize: Float): Float = verticalSize / 2f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize / 2f
    },
    CENTER_RIGHT {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize
        override fun getRightMargin(horizontalSize: Float): Float = 0f
        override fun getTopMargin(verticalSize: Float): Float = verticalSize / 2f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize / 2f
    },
    LEFT_TOP {
        override fun getLeftMargin(horizontalSize: Float): Float = 0f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize
        override fun getTopMargin(verticalSize: Float): Float = 0f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize
    },
    LEFT_BOTTOM {
        override fun getLeftMargin(horizontalSize: Float): Float = 0f
        override fun getRightMargin(horizontalSize: Float): Float = horizontalSize
        override fun getTopMargin(verticalSize: Float): Float = verticalSize
        override fun getBottomMargin(verticalSize: Float): Float = 0f
    },
    RIGHT_TOP {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize
        override fun getRightMargin(horizontalSize: Float): Float = 0f
        override fun getTopMargin(verticalSize: Float): Float = 0f
        override fun getBottomMargin(verticalSize: Float): Float = verticalSize
    },
    RIGHT_BOTTOM {
        override fun getLeftMargin(horizontalSize: Float): Float = horizontalSize
        override fun getRightMargin(horizontalSize: Float): Float = 0f
        override fun getTopMargin(verticalSize: Float): Float = verticalSize
        override fun getBottomMargin(verticalSize: Float): Float = 0f
    };

    abstract fun getLeftMargin(horizontalSize: Float) : Float
    abstract fun getRightMargin(horizontalSize: Float) : Float
    abstract fun getTopMargin(verticalSize: Float) : Float
    abstract fun getBottomMargin(verticalSize: Float) : Float
}