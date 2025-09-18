package org.example.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * PUBLIC_INTERFACE
 * MainActivity displays a minimalist color palette generator with five stacked color tiles.
 * Users can generate new palettes (random HSL) via the floating action button and
 * copy individual color codes by tapping the copy icon on each tile.
 *
 * UI:
 * - Five vertically stacked tiles (rounded corners) show the color swatch and its hex/HSL codes.
 * - A floating action button at the bottom generates a new palette.
 *
 * Returns: No direct return; the Activity manages the UI lifecycle and interactions.
 */
class MainActivity : Activity() {

    // Ocean Professional theme palette constants
    private val oceanPrimary = Color.parseColor("#374151")   // primary
    private val oceanSecondary = Color.parseColor("#9CA3AF") // secondary
    private val oceanSuccess = Color.parseColor("#10B981")   // success (used for accents/feedback)
    private val oceanError = Color.parseColor("#EF4444")     // error
    private val oceanBackground = Color.parseColor("#FFFFFF")
    private val oceanSurface = Color.parseColor("#F9FAFB")
    private val oceanText = Color.parseColor("#111827")

    // Tile root views and labels
    private lateinit var tileRoots: List<View>
    private lateinit var hexLabels: List<TextView>
    private lateinit var hslLabels: List<TextView>
    private lateinit var copyButtons: List<ImageButton>
    private lateinit var generateFab: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use our custom layout
        setContentView(R.layout.activity_main)

        // Initialize view references
        tileRoots = listOf(
            findViewById(R.id.tile1),
            findViewById(R.id.tile2),
            findViewById(R.id.tile3),
            findViewById(R.id.tile4),
            findViewById(R.id.tile5)
        )

        hexLabels = listOf(
            findViewById(R.id.hex1),
            findViewById(R.id.hex2),
            findViewById(R.id.hex3),
            findViewById(R.id.hex4),
            findViewById(R.id.hex5)
        )

        hslLabels = listOf(
            findViewById(R.id.hsl1),
            findViewById(R.id.hsl2),
            findViewById(R.id.hsl3),
            findViewById(R.id.hsl4),
            findViewById(R.id.hsl5)
        )

        copyButtons = listOf(
            findViewById(R.id.copy1),
            findViewById(R.id.copy2),
            findViewById(R.id.copy3),
            findViewById(R.id.copy4),
            findViewById(R.id.copy5)
        )

        generateFab = findViewById(R.id.fab_generate)

        // Themed backgrounds
        val root: View = findViewById(R.id.root_container)
        root.setBackgroundColor(oceanBackground)
        val tilesContainer: LinearLayout = findViewById(R.id.tiles_container)
        tilesContainer.setBackgroundColor(oceanSurface)

        // Set copy listeners
        copyButtons.forEachIndexed { index, btn ->
            btn.setOnClickListener {
                val textToCopy = hexLabels[index].text.toString()
                copyToClipboard(textToCopy)
                showToast("Copied ${textToCopy}")
            }
        }

        // FAB listener
        generateFab.setOnClickListener {
            generateAndApplyPalette()
        }

        // Initial palette
        generateAndApplyPalette()
    }

    // PUBLIC_INTERFACE
    /**
     * Generate a new random palette (5 colors) using random HSL values and apply it to the tiles.
     * H range: 0..360, S range: 40..85, L range: 35..75 for pleasing pastel/professional tones.
     */
    fun generateAndApplyPalette() {
        val colors = (0 until 5).map { _ ->
            val h = (0..360).random()
            val s = (40..85).random()
            val l = (35..75).random()
            hslToColor(h, s, l)
        }

        colors.forEachIndexed { index, color ->
            val hex = colorToHex(color)
            val (h, s, l) = colorToHsl(color)

            // Set swatch background
            tileRoots[index].setBackgroundColor(color)

            // Choose foreground based on luminance
            val onColor = if (isColorDark(color)) Color.WHITE else oceanText

            hexLabels[index].apply {
                text = hex
                setTextColor(onColor)
            }
            hslLabels[index].apply {
                text = "HSL(${h}, ${s}%, ${l}%)"
                setTextColor(onColor)
            }

            copyButtons[index].apply {
                setColorFilter(onColor)
                background?.alpha = 0 // keep background clean
                contentDescription = "Copy ${hex}"
            }
        }
    }

    // PUBLIC_INTERFACE
    /** Copy text to Android clipboard with a labeled clip. */
    fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("color_code", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showToast(message: String) {
        val t = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        // Optional success tint hint: toast text only
        t.show()
    }

    // PUBLIC_INTERFACE
    /** Convert HSL components to ARGB color int. h:0-360, s:0-100, l:0-100 */
    fun hslToColor(h: Int, s: Int, l: Int): Int {
        val hh = (h % 360) / 360f
        val ss = (s.coerceIn(0, 100)) / 100f
        val ll = (l.coerceIn(0, 100)) / 100f

        val r: Float
        val g: Float
        val b: Float

        if (ss == 0f) {
            r = ll
            g = ll
            b = ll
        } else {
            val q = if (ll < 0.5f) ll * (1 + ss) else (ll + ss) - (ll * ss)
            val p = 2 * ll - q
            r = hueToRgb(p, q, hh + 1f / 3f)
            g = hueToRgb(p, q, hh)
            b = hueToRgb(p, q, hh - 1f / 3f)
        }

        val rr = (r * 255.0f + 0.5f).toInt()
        val gg = (g * 255.0f + 0.5f).toInt()
        val bb = (b * 255.0f + 0.5f).toInt()
        return Color.rgb(rr, gg, bb)
    }

    private fun hueToRgb(p: Float, q: Float, tIn: Float): Float {
        var t = tIn
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        return when {
            t < 1f / 6f -> p + (q - p) * 6f * t
            t < 1f / 2f -> q
            t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
            else -> p
        }
    }

    // PUBLIC_INTERFACE
    /** Convert ARGB color int to HEX string like #RRGGBB. */
    fun colorToHex(color: Int): String {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format("#%02X%02X%02X", r, g, b)
    }

    // PUBLIC_INTERFACE
    /** Convert ARGB color int to HSL triplet (H:0-360, S:0-100, L:0-100). */
    fun colorToHsl(color: Int): Triple<Int, Int, Int> {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val l = (max + min) / 2f

        var h: Float
        val s: Float

        if (max == min) {
            h = 0f
            s = 0f
        } else {
            val d = max - min
            s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
            h = when (max) {
                r -> (g - b) / d + (if (g < b) 6f else 0f)
                g -> (b - r) / d + 2f
                else -> (r - g) / d + 4f
            }
            h /= 6f
        }

        val hi = ((h * 360f) + 0.5f).toInt()
        val si = ((s * 100f) + 0.5f).toInt()
        val li = ((l * 100f) + 0.5f).toInt()
        return Triple(hi, si, li)
    }

    private fun isColorDark(color: Int): Boolean {
        // perceived luminance approximation
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
        return luminance < 0.6
    }
}
