package org.example.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PaletteGeneratorTestJvm {

    @Test
    fun generator_returns_five_visually_distinct_colors() {
        val gen = PaletteGenerator()
        val palette = gen.generatePalette(5)
        assertEquals(5, palette.size)
        val hexSet = palette.map { it.toHex() }.toSet()
        assertEquals(5, hexSet.size)
        palette.forEach {
            assertTrue(it.h in 0..360)
            assertTrue(it.s in 0..100)
            assertTrue(it.l in 0..100)
        }
    }
}
