package org.example.app

import kotlin.random.Random
import kotlin.math.abs
import kotlin.system.measureTimeMillis

/**
 * PUBLIC_INTERFACE
 * PaletteGenerator creates palettes of visually distinct HSL colors.
 * - Supports variable palette size
 * - Enforces minimum thresholds for visual distinctness
 * - Extensible for other color models in future
 */
class PaletteGenerator(
    private val hueMinDiff: Int = 30,
    private val satMinDiff: Int = 15,
    private val lightMinDiff: Int = 15,
    private val maxAttempts: Int = 200
) {

    /**
     * PUBLIC_INTERFACE
     * Generate a palette with the given size.
     * Ensures distinctness across H, S, and L using configured thresholds.
     */
    fun generatePalette(size: Int = 5): List<HslColor> {
        require(size > 0) { "Palette size must be > 0" }
        val out = mutableListOf<HslColor>()
        var attempts = 0

        while (out.size < size && attempts < maxAttempts) {
            attempts++
            val candidate = randomDesignFriendly()
            if (out.none { tooSimilar(it, candidate) }) {
                out.add(candidate)
            }
        }
        if (out.size < size) {
            // Fallback: relax thresholds slightly to finish palette.
            while (out.size < size) {
                val candidate = randomDesignFriendly()
                if (out.none { relaxedSimilar(it, candidate) }) out.add(candidate)
            }
        }
        return out
    }

    private fun randomDesignFriendly(): HslColor {
        val h = Random.nextInt(0, 361)
        // Keep saturation and lightness away from extremes for design-friendly colors
        val s = Random.nextInt(45, 91)
        val l = Random.nextInt(30, 75)
        return HslColor(h, s, l)
    }

    private fun tooSimilar(a: HslColor, b: HslColor): Boolean {
        val hueDiff = hueDistance(a.h, b.h)
        val sDiff = abs(a.s - b.s)
        val lDiff = abs(a.l - b.l)
        return hueDiff < hueMinDiff || sDiff < satMinDiff || lDiff < lightMinDiff
    }

    private fun relaxedSimilar(a: HslColor, b: HslColor): Boolean {
        val hueDiff = hueDistance(a.h, b.h)
        val sDiff = abs(a.s - b.s)
        val lDiff = abs(a.l - b.l)
        return hueDiff < hueMinDiff / 2 || sDiff < satMinDiff / 2 || lDiff < lightMinDiff / 2
    }

    private fun hueDistance(a: Int, b: Int): Int {
        val d = abs(a - b)
        return minOf(d, 360 - d)
    }
}
