package com.optativa.dogage.logic

import java.text.Normalizer
import kotlin.math.roundToInt

/** Perro guardado en "Mis perros". */
data class Dog(
    val id: Int,
    val name: String,
    val size: DogSize,
    val years: Int,
    val months: Int,
    val mode: CalcMode = CalcMode.ADVANCED
) {
    val age: AgeInput get() = AgeInput(years, months)
}

/** Resumen calculado de un perro (para mostrar en la lista). */
data class DogSummary(val dog: Dog, val result: DogAgeResult) {
    val humanYearsRounded: Int get() = result.humanYears.roundToInt()
}

/** Indicadores de la pantalla "Mis perros". */
data class DogStats(
    val total: Int,
    val averageHumanYears: Int,
    val oldest: Dog?,
    val seniors: Int,
    val puppies: Int
)

object DogValidator {
    const val MAX_DOGS = 20
    const val MAX_NAME = 20

    private fun key(name: String): String =
        Normalizer.normalize(name.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

    /** Devuelve el primer error o null si se puede guardar. */
    fun validate(
        name: String,
        yearsText: String,
        monthsText: String,
        existing: List<Dog>,
        editingId: Int? = null
    ): String? {
        val clean = name.trim()
        if (clean.length < 2) return "El nombre debe tener al menos 2 letras."
        if (clean.length > MAX_NAME) return "El nombre puede tener máximo $MAX_NAME caracteres."
        if (!clean.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' }) {
            return "El nombre solo puede tener letras, espacios o guiones."
        }
        if (existing.any { it.id != editingId && key(it.name) == key(clean) }) {
            return "Ya tienes un perro llamado «$clean»."
        }
        if (editingId == null && existing.size >= MAX_DOGS) return "Puedes guardar máximo $MAX_DOGS perros."
        val parsed = DogAgeCalculator.parseAge(yearsText, monthsText)
        return (parsed as? ParseResult.Error)?.message
    }
}

object DogOps {
    fun nextId(dogs: List<Dog>): Int = (dogs.maxOfOrNull { it.id } ?: 0) + 1

    /** Inserta o reemplaza por id. */
    fun upsert(dogs: List<Dog>, dog: Dog): List<Dog> =
        if (dogs.any { it.id == dog.id }) dogs.map { if (it.id == dog.id) dog else it } else dogs + dog

    fun delete(dogs: List<Dog>, id: Int): List<Dog> = dogs.filterNot { it.id == id }

    /** Suma meses a la edad (botón "Cumplió un mes / un año"), sin superar el máximo. */
    fun addMonths(dog: Dog, months: Int): Dog {
        val total = (dog.years * 12 + dog.months + months).coerceIn(1, DogAgeCalculator.MAX_DOG_YEARS * 12 + 11)
        val y = (total / 12).coerceAtMost(DogAgeCalculator.MAX_DOG_YEARS)
        return dog.copy(years = y, months = total - y * 12)
    }

    fun summary(dog: Dog): DogSummary = DogSummary(dog, DogAgeCalculator.calculate(dog.age, dog.size, dog.mode))

    /** Orden alfabético sin importar tildes ni mayúsculas. */
    fun sorted(dogs: List<Dog>): List<Dog> = dogs.sortedBy {
        Normalizer.normalize(it.name.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun stats(dogs: List<Dog>): DogStats {
        val summaries = dogs.map { summary(it) }
        return DogStats(
            total = dogs.size,
            averageHumanYears = if (summaries.isEmpty()) 0 else summaries.map { it.result.humanYears }.average().roundToInt(),
            oldest = dogs.maxByOrNull { it.age.totalYears },
            seniors = summaries.count { it.result.stage == LifeStage.SENIOR },
            puppies = summaries.count { it.result.stage == LifeStage.PUPPY }
        )
    }
}
