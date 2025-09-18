package org.example.app

/**
 * PUBLIC_INTERFACE
 * Data class representing an HSL color.
 * h in [0,360], s in [0,100], l in [0,100]
 */
data class HslColor(val h: Int, val s: Int, val l: Int) {
    /** Convert HSL to HEX string (without leading #) */
    fun toHex(): String {
        val rgb = hslToRgb(h.toFloat(), s / 100f, l / 100f)
        return "%02X%02X%02X".format(rgb.first, rgb.second, rgb.third)
    }

    companion object {
        /**
         * Convert HSL to RGB triple (0..255).
         * Algorithm adapted from standard formulas.
         */
        fun hslToRgb(h: Float, s: Float, l: Float): Triple<Int, Int, Int> {
            val c = (1 - kotlin.math.abs(2 * l - 1)) * s
            val hp = (h / 60f) % 6f
            val x = c * (1 - kotlin.math.abs(hp % 2 - 1))
            val (r1, g1, b1) = when {
                hp < 1 -> Triple(c, x, 0f)
                hp < 2 -> Triple(x, c, 0f)
                hp < 3 -> Triple(0f, c, x)
                hp < 4 -> Triple(0f, x, c)
                hp < 5 -> Triple(x, 0f, c)
                else -> Triple(c, 0f, x)
            }
            val m = l - c / 2f
            val r = ((r1 + m) * 255).toInt().coerceIn(0, 255)
            val g = ((g1 + m) * 255).toInt().coerceIn(0, 255)
            val b = ((b1 + m) * 255).toInt().coerceIn(0, 255)
            return Triple(r, g, b)
        }
    }
}
