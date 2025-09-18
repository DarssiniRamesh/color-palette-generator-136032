package org.example.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * Minimal single-activity app that:
 * - Generates 5 random HSL colors on launch
 * - Displays them as colored blocks with HSL and HEX labels beneath each
 * - Has a single "Generate Palette" button to regenerate the palette
 * No animations, copy actions, or advanced theming in this minimal version.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tilesContainer: LinearLayout
    private lateinit var generateButton: Button

    private val generator = PaletteGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tilesContainer = findViewById(R.id.tilesContainer)
        generateButton = findViewById(R.id.generateButton)

        // Initial palette on launch
        renderPalette(generator.generatePalette(5))

        // Regenerate on button click
        generateButton.setOnClickListener {
            renderPalette(generator.generatePalette(5))
        }
    }

    private fun renderPalette(palette: List<HslColor>) {
        val tileIds = listOf(R.id.tile1, R.id.tile2, R.id.tile3, R.id.tile4, R.id.tile5)
        tileIds.zip(palette).forEach { (tileId, color) ->
            val tile = findViewById<ViewGroup>(tileId)
            val colorView = tile.findViewById<View>(R.id.tileColor)
            val textHsl = tile.findViewById<TextView>(R.id.textHsl)
            val textHex = tile.findViewById<TextView>(R.id.textHex)

            val hex = color.toHex()
            val hslText = "hsl(${color.h}, ${color.s}%, ${color.l}%)"
            val hexText = "#$hex"

            colorView.setBackgroundColor(Color.parseColor(hexText))
            textHsl.text = hslText
            textHex.text = hexText
        }
    }
}
