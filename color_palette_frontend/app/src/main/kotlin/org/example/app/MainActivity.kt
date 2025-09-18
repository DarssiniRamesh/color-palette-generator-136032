package org.example.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Random

/**
 * PUBLIC_INTERFACE
 * MainActivity provides a minimalist interface to generate and display a 5-color palette.
 * - Shows five vertically stacked, rounded tiles with color values.
 * - Each tile has a copy button to copy the color code.
 * - Floating Action Button regenerates the palette locally using random HSL.
 */
class MainActivity : AppCompatActivity() {

    private val random = Random()

    // Views for color tiles and their labels
    private lateinit var tiles: List<View>
    private lateinit var labels: List<TextView>
    private lateinit var copyButtons: List<ImageButton>
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tiles = listOf(
            findViewById(R.id.tile1),
            findViewById(R.id.tile2),
            findViewById(R.id.tile3),
            findViewById(R.id.tile4),
            findViewById(R.id.tile5)
        )

        labels = listOf(
            findViewById(R.id.label1),
            findViewById(R.id.label2),
            findViewById(R.id.label3),
            findViewById(R.id.label4),
            findViewById(R.id.label5)
        )

        copyButtons = listOf(
            findViewById(R.id.copy1),
            findViewById(R.id.copy2),
            findViewById(R.id.copy3),
            findViewById(R.id.copy4),
            findViewById(R.id.copy5)
        )

        fab = findViewById(R.id.fab)

        fab.setOnClickListener {
            populateRandomPalette()
        }

        // Initial render
        populateRandomPalette()
    }

    // PUBLIC_INTERFACE
    /**
     * Generates and applies a random 5-color palette using HSL values.
     * H ranges 0..360, S and L in ranges to ensure visually pleasing colors.
     */
    private fun populateRandomPalette() {
        for (i in tiles.indices) {
            val h = random.nextInt(360)
            val s = 50 + random.nextInt(40) // 50..89
            val l = 45 + random.nextInt(30) // 45..74

            val color = hslToColor(h.toFloat(), s / 100f, l / 100f)
            val hex = toHex(color)

            applyTile(i, color, hex)
        }
    }

    private fun applyTile(index: Int, @ColorInt color: Int, hex: String) {
        val tile = tiles[index]
        val label = labels[index]
        val copy = copyButtons[index]

        tile.setBackgroundColor(color)
        label.text = hex

        // Set content color (text/icon) to be accessible based on contrast
        val onColor = if (isColorDark(color)) Color.WHITE else Color.parseColor("#111827")
        label.setTextColor(onColor)
        copy.imageTintList = android.content.res.ColorStateList.valueOf(onColor)

        // Copy interaction
        copy.setOnClickListener {
            copyToClipboard(hex)
            Toast.makeText(this, "Copied $hex", Toast.LENGTH_SHORT).show()
        }

        // Elevation for subtle separation when background similar
        ViewCompat.setElevation(tile, 2f)
    }

    // PUBLIC_INTERFACE
    /** Convert HSL to Android color int. */
    @ColorInt
    private fun hslToColor(h: Float, s: Float, l: Float): Int {
        // Convert HSL to RGB using common algorithm
        val c = (1 - kotlin.math.abs(2 * l - 1)) * s
        val hh = h / 60f
        val x = c * (1 - kotlin.math.abs(hh % 2 - 1))
        val (r1, g1, b1) = when {
            hh < 1 -> Triple(c, x, 0f)
            hh < 2 -> Triple(x, c, 0f)
            hh < 3 -> Triple(0f, c, x)
            hh < 4 -> Triple(0f, x, c)
            hh < 5 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        val m = l - c / 2
        val r = ((r1 + m) * 255).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    private fun toHex(@ColorInt color: Int): String {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return String.format("#%02X%02X%02X", r, g, b)
    }

    private fun isColorDark(@ColorInt color: Int): Boolean {
        // Per W3C relative luminance
        val r = Color.red(color) / 255.0
        val g = Color.green(color) / 255.0
        val b = Color.blue(color) / 255.0

        fun channel(c: Double): Double =
            if (c <= 0.03928) c / 12.92 else Math.pow((c + 0.055) / 1.055, 2.4)

        val l = 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
        return l < 0.5
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Color", text)
        clipboard.setPrimaryClip(clip)
    }
}
