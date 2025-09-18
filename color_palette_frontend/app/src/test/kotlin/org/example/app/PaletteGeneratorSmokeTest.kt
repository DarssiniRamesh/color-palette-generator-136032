package org.example.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaletteGeneratorSmokeTest {

    @Test
    fun `generator produces five colors`() {
        val gen = PaletteGenerator()
        val palette = gen.generatePalette()
        assertEquals(5, palette.size)
    }
}
