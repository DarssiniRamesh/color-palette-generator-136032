package org.example.app

import android.animation.TimeInterpolator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong

/**
 * PUBLIC_INTERFACE
 * MainActivity
 * This is the entry point of the Color Palette Generator app.
 * It generates a random palette of visually distinct HSL colors at launch and on demand,
 * displays five vertical tiles with HSL and HEX values, supports copy-to-clipboard per tile,
 * animates transitions, and allows theme switching (light/dark).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tilesContainer: LinearLayout
    private lateinit var fabGenerate: FloatingActionButton
    private lateinit var themeToggle: ImageButton

    private val uiScope = MainScope()
    private val debouncer = AtomicLong(0L)
    private val debounceWindowMs = 400L

    private val generator = PaletteGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tilesContainer = findViewById(R.id.tilesContainer)
        fabGenerate = findViewById(R.id.fabGenerate)
        themeToggle = findViewById(R.id.btnTheme)

        // Accessibility content descriptions
        fabGenerate.contentDescription = getString(R.string.cd_generate_palette)
        themeToggle.contentDescription = getString(R.string.cd_toggle_theme)

        fabGenerate.setOnClickListener {
            if (requestAllowed()) {
                generateAndRenderPalette(animated = true)
            }
        }

        themeToggle.setOnClickListener {
            toggleTheme()
        }

        // Initial palette
        generateAndRenderPalette(animated = false)
    }

    private fun requestAllowed(): Boolean {
        val now = System.currentTimeMillis()
        val last = debouncer.get()
        return if (now - last > debounceWindowMs) {
            debouncer.set(now)
            true
        } else {
            false
        }
    }

    private fun toggleTheme() {
        val current = AppCompatDelegate.getDefaultNightMode()
        val next = if (current == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(next)
        // Provide feedback
        Snackbar.make(
            tilesContainer,
            if (next == AppCompatDelegate.MODE_NIGHT_YES) getString(R.string.dark_mode_on) else getString(R.string.dark_mode_off),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun generateAndRenderPalette(animated: Boolean) {
        fabGenerate.isEnabled = false
        uiScope.launch {
            val palette = withContext(Dispatchers.Default) {
                generator.generatePalette(size = 5)
            }
            renderPalette(palette, animated)
            fabGenerate.isEnabled = true
        }
    }

    private fun renderPalette(palette: List<HslColor>, animated: Boolean) {
        // Ensure we have exactly 5 tile containers (defined in layout)
        val tileIds = listOf(
            R.id.tile1, R.id.tile2, R.id.tile3, R.id.tile4, R.id.tile5
        )

        tileIds.zip(palette).forEach { (tileId, color) ->
            val tile = findViewById<ViewGroup>(tileId)
            val hex = color.toHex()
            val hslText = "hsl(${color.h}, ${color.s}%, ${color.l}%)"
            val hexText = "#$hex"

            val colorView = tile.findViewById<View>(R.id.tileColor)
            val textHsl = tile.findViewById<TextView>(R.id.textHsl)
            val textHex = tile.findViewById<TextView>(R.id.textHex)
            val btnCopyHsl = tile.findViewById<ImageButton>(R.id.btnCopyHsl)
            val btnCopyHex = tile.findViewById<ImageButton>(R.id.btnCopyHex)

            // Background color
            colorView.setBackgroundColor(Color.parseColor("#$hex"))

            // Choose text color based on lightness for contrast
            val textColor = if (color.l >= 60) ContextCompat.getColor(this, R.color.ocean_text_dark)
            else ContextCompat.getColor(this, R.color.ocean_text_light)

            textHsl.setTextColor(textColor)
            textHex.setTextColor(textColor)

            textHsl.text = hslText
            textHex.text = hexText

            // Content descriptions for accessibility
            btnCopyHsl.contentDescription = getString(R.string.cd_copy_hsl, hslText)
            btnCopyHex.contentDescription = getString(R.string.cd_copy_hex, hexText)

            btnCopyHsl.setOnClickListener { copyToClipboard("HSL", hslText, it) }
            btnCopyHex.setOnClickListener { copyToClipboard("HEX", hexText, it) }

            if (animated) {
                crossfade(colorView)
            }
        }
    }

    private fun crossfade(target: View, duration: Long = 220L, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
        // Respect reduced motion: if animator duration scale is 0, skip animations.
        val animationsEnabled = ViewCompat.isLaidOut(target) && (android.provider.Settings.Global.getFloat(
            contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f
        ) > 0f)

        if (!animationsEnabled) return

        target.alpha = 0f
        target.isVisible = true
        target.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .start()
    }

    private fun copyToClipboard(label: String, text: String, anchor: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(anchor, getString(R.string.copied_to_clipboard, label), Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
