package org.example.app

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PaletteGeneratorTest {

    @Test
    fun `generates exactly 5 colors by default`() {
        val gen = PaletteGenerator()
        val palette = gen.generatePalette()
        assertEquals(5, palette.size)
    }

    @Test
    fun `all colors are within valid HSL ranges`() {
        val gen = PaletteGenerator()
        val palette = gen.generatePalette(5)
        palette.forEach {
            assertTrue(it.h in 0..360, "Hue out of range")
            assertTrue(it.s in 0..100, "Saturation out of range")
            assertTrue(it.l in 0..100, "Lightness out of range")
        }
    }

    @Test
    fun `colors are visually distinct`() {
        val gen = PaletteGenerator()
        val palette = gen.generatePalette(5)
        // Basic distinctness: toHex unique
        val hexes = palette.map { it.toHex() }
        assertEquals(hexes.size, hexes.toSet().size, "Duplicate HEX colors detected")
    }

    @Test
    fun `rapid consecutive generation succeeds`() {
        val gen = PaletteGenerator()
        repeat(10) {
            val palette = gen.generatePalette(5)
            assertEquals(5, palette.size)
        }
    }
}
