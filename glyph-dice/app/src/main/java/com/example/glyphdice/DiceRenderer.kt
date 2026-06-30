package com.example.glyphdice

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Draws a dice face onto a square bitmap sized for the Glyph Matrix.
 *
 * The Nothing Phone (4a) Pro matrix is 13x13 LEDs. Each LED is monochrome white
 * with adjustable brightness, so the SDK maps the bitmap's luminance to LED
 * brightness — white pixels = bright LED, black = off. We therefore draw white
 * pips on a black background.
 */
object DiceRenderer {

    /** Matrix edge length for Phone (4a) Pro. Phone (3) would be 25. */
    const val MATRIX_SIZE = 13

    // Pip anchor coordinates on the 13x13 grid (a 3x3 conceptual layout).
    private const val LOW = 3f
    private const val MID = 6f
    private const val HIGH = 9f
    private const val PIP_RADIUS = 1.6f

    /**
     * @param value die face 1..6
     * @return a [MATRIX_SIZE] x [MATRIX_SIZE] bitmap of the face.
     */
    fun renderFace(value: Int): Bitmap {
        val bmp = Bitmap.createBitmap(MATRIX_SIZE, MATRIX_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.BLACK)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        for ((x, y) in pipsFor(value)) {
            canvas.drawCircle(x, y, PIP_RADIUS, paint)
        }
        return bmp
    }

    /** Standard dice pip layout for each face. */
    private fun pipsFor(value: Int): List<Pair<Float, Float>> = when (value.coerceIn(1, 6)) {
        1 -> listOf(MID to MID)
        2 -> listOf(LOW to LOW, HIGH to HIGH)
        3 -> listOf(LOW to LOW, MID to MID, HIGH to HIGH)
        4 -> listOf(LOW to LOW, HIGH to LOW, LOW to HIGH, HIGH to HIGH)
        5 -> listOf(LOW to LOW, HIGH to LOW, MID to MID, LOW to HIGH, HIGH to HIGH)
        6 -> listOf(LOW to LOW, HIGH to LOW, LOW to MID, HIGH to MID, LOW to HIGH, HIGH to HIGH)
        else -> emptyList()
    }
}
