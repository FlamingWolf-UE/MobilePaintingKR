package com.example.paintingapplab3.model

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import java.util.*

class FloodFillModel {

    private var width = 0
    private var height = 0
    private var fillColor: Int = -1
    private var startColor = intArrayOf(0, 0, 0)

    private lateinit var image: Bitmap
    private lateinit var pixels: IntArray
    private lateinit var pixelsChecked: BooleanArray
    private lateinit var ranges: Queue<FloodFillRange>

    fun floodFill( x: Int, y: Int, targetColor: Int, replacementColor: Int) {

           queueLinearFloodFill(x, y, targetColor, replacementColor)


    }

    fun useImage(img: Bitmap) {
        image = img
        width = img.width
        height = img.height
        pixels = IntArray(width * height)

        image.getPixels(pixels, 0, width, 0, 0, width, height)
    }


    private fun queueLinearFloodFill(x: Int, y: Int, targetColor: Int, replacementColor: Int) {
        if(targetColor == replacementColor) return

        setTargetColor(targetColor)
        fillColor = replacementColor

        prepare()

        if (startColor[0] == 0) {
            val startPixel = pixels[width * y + x]
            startColor[0] = startPixel shr 16 and 0xff
            startColor[1] = startPixel shr 8 and 0xff
            startColor[2] = startPixel and 0xff
        }

        linearFill(x, y)

        var range: FloodFillRange

        while (ranges.size > 0) {
            range = ranges.remove()

            var downPxIdx = width * (range.Y + 1) + range.startX
            var upPxIdx = width * (range.Y - 1) + range.startX
            val upY = range.Y - 1
            val downY = range.Y + 1

            for (i in range.startX..range.endX) {
                if (range.Y > 0 && !pixelsChecked[upPxIdx]
                    && checkPixel(upPxIdx))
                    linearFill(i, upY)

                if (range.Y < height - 1 && !pixelsChecked[downPxIdx]
                    && checkPixel(downPxIdx))
                    linearFill(i, downY)

                downPxIdx++
                upPxIdx++
            }
        }

        image.setPixels(pixels, 0, width, 0, 0, width , height )
    }


    private fun setTargetColor(targetColor: Int) {
        startColor[0] = Color.red(targetColor)
        startColor[1] = Color.green(targetColor)
        startColor[2] = Color.blue(targetColor)
    }

    private fun prepare() {
        pixelsChecked = BooleanArray(pixels.size)
        ranges = LinkedList()
    }

    private fun linearFill(x: Int, y: Int) {
        var lFillLoc = x
        var pxIdx = width * y + x

        while (true) {
            pixels[pxIdx] = fillColor
            pixelsChecked[pxIdx] = true

            lFillLoc--
            pxIdx--

            if (lFillLoc < 0 || pixelsChecked[pxIdx] || !checkPixel(pxIdx)) {
                break
            }
        }

        lFillLoc++

        var rFillLoc = x

        pxIdx = width * y + x

        while (true) {
            pixels[pxIdx] = fillColor
            pixelsChecked[pxIdx] = true

            rFillLoc++
            pxIdx++

            if (rFillLoc >= width || pixelsChecked[pxIdx] || !checkPixel(pxIdx)) {
                break
            }
        }

        rFillLoc--

        val r = FloodFillRange(lFillLoc, rFillLoc, y)

        ranges.offer(r)
    }

    private fun checkPixel(px: Int): Boolean {
        val red = pixels[px].ushr(16) and 0xff
        val green = pixels[px].ushr(8) and 0xff
        val blue = pixels[px] and 0xff

        return (red >= startColor[0] && red <= startColor[0] &&
                green >= startColor[1] && green <= startColor[1] &&
                blue >= startColor[2] && blue <= startColor[2])
    }

    private inner class FloodFillRange(var startX: Int, var endX: Int, var Y: Int)

    private fun clamp(value: Int, min: Int, max: Int): Int = Math.max(min, Math.min(value, max))

}