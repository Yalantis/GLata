package com.yalantis.glata.model

import com.yalantis.glata.primitive.Color


class SegmentedPlane(width: Float, height: Float, segsW: Int, segsH: Int, texW: Int = 1, texH: Int = 1, horizontal: Boolean = true) : Model() {

    init {
        build(width, height, segsW, segsH, texW, texH, horizontal)
    }

    private fun build(width: Float, height: Float, segsW: Int, segsH: Int, texW: Int, texH: Int, horizontal: Boolean) {
        val vertexBufferSize = 12 * segsW * segsH
        val texCoordsBufferSize = 8 * segsW * segsH
//        val colorBufferSize = 16 * segsW * segsH
        val indexBufferSize = 6 * segsW * segsH

        val vertexArray = FloatArray(vertexBufferSize)
        val texCoordArray = FloatArray(texCoordsBufferSize)
//        val colorArray = FloatArray(colorBufferSize)
        val indexArray = ShortArray(indexBufferSize)

        var row: Int
        var col: Int
        var counter = 0

        val w = width / segsW
        val h = height / segsH

        val width5 = width / 2f
        val height5 = height / 2f

        row = 0
        while (row <= segsH) {
            col = 0
            while (col <= segsW) {
                vertexArray[counter * 3] = col.toFloat() * w - width5
                if (horizontal)
                    vertexArray[counter * 3 + 1] = row.toFloat() * h - height5
                else
                    vertexArray[counter * 3 + 1] = 0f

                if (horizontal)
                    vertexArray[counter * 3 + 2] = 0f
                else
                    vertexArray[counter * 3 + 2] = row.toFloat() * h - height5


                texCoordArray[counter * 2] = col.toFloat() / segsW.toFloat() * texW.toFloat() % 2f
                texCoordArray[counter * 2 + 1] = 1 - row.toFloat() / segsH.toFloat() * texH.toFloat()

//                colorArray[counter] = color.r
//                colorArray[counter + 1] = color.g
//                colorArray[counter + 2] = color.b
//                colorArray[counter + 3] = color.a

                counter++
                col++
            }
            row++
        }

        val colspan = segsW + 1
        counter = 0

        row = 1
        while (row <= segsH) {
            col = 1
            while (col <= segsW) {
                val lr = row * colspan + col
                val ll = lr - 1
                val ur = lr - colspan
                val ul = ur - 1
                indexArray[counter] = ul.toShort()
                counter++
                indexArray[counter] = lr.toShort()
                counter++
                indexArray[counter] = ur.toShort()
                counter++
                indexArray[counter] = ul.toShort()
                counter++
                indexArray[counter] = ll.toShort()
                counter++
                indexArray[counter] = lr.toShort()
                counter++
                col++
            }
            row++
        }

        geometry.createGeometry(vertexArray, texCoordArray, null, null, indexArray)
        geometry.allocateAll()
        geometry.putAllFromInnerArrays()
        geometry.deleteArrays()
        geometry.isDoubleSideEnabled = true
    }
}