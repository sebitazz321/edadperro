package com.optativa.dogage

import com.optativa.dogage.logic.DogBot
import org.junit.Assert.assertTrue
import org.junit.Test

class DogBotTest {

    @Test
    fun understandsYears() {
        val r = DogBot().reply("Mi perro tiene 5 años")
        assertTrue(r, r.contains("35") || r.contains("36"))   // avanzado: 24 + 5*3 = 39 / simple: 35
        assertTrue(r, r.contains("5 años"))
    }

    @Test
    fun understandsYearsMonthsAndSize() {
        val r = DogBot().reply("tiene 2 años y 6 meses, es grande")
        assertTrue(r, r.contains("grande"))
        assertTrue(r, r.contains("2 años y 6 meses"))
    }

    @Test
    fun remembersSize() {
        val bot = DogBot()
        bot.reply("es un perro gigante")
        val r = bot.reply("tiene 8 años")
        assertTrue(r, r.contains("gigante"))
    }

    @Test
    fun rejectsAbsurdAge() {
        assertTrue(DogBot().reply("tiene 99 años").contains("no me cuadra"))
    }

    @Test
    fun answersTopics() {
        val bot = DogBot()
        assertTrue(bot.reply("¿Qué vacunas necesita?").contains("vacuna", ignoreCase = true))
        assertTrue(bot.reply("¿Cómo se calcula?").contains("Simple"))
        assertTrue(bot.reply("¿Cuánto vive un perro?").contains("Promedio"))
        assertTrue(bot.reply("Hola").contains("Hola"))
    }

    @Test
    fun fallbackForUnknown() {
        assertTrue(DogBot().reply("asdfgh").contains("No estoy seguro"))
    }
}
