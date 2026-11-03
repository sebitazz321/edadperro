package com.optativa.dogage.logic

import kotlin.math.ln
import kotlin.math.min

/** Tamaño del perro: define el ritmo de envejecimiento y la esperanza de vida. */
enum class DogSize(
    val label: String,
    val emoji: String,
    val yearlyRate: Double,      // años humanos por cada año canino después del 2.º año
    val seniorFrom: Double,      // edad canina desde la cual se considera senior
    val lifeExpectancy: Double   // esperanza de vida aproximada (años)
) {
    SMALL("Pequeño", "🐩", 4.0, 10.0, 14.0),
    MEDIUM("Mediano", "🐕", 5.0, 9.0, 13.0),
    LARGE("Grande", "🦮", 6.0, 8.0, 11.0),
    GIANT("Gigante", "🐾", 7.0, 6.0, 9.0)
}

/** Método de cálculo. */
enum class CalcMode(val label: String, val description: String) {
    SIMPLE("Simple", "Regla clásica: 1 año de perro = 7 años humanos."),
    ADVANCED(
        "Avanzado",
        "Modelo no lineal (guía AKC): el 1.er año = 15, el 2.º = +9 y desde el 3.º suma según el tamaño."
    ),
    SCIENTIFIC(
        "Científico",
        "Fórmula de la Universidad de California (epigenética): 16 · ln(edad) + 31."
    )
}

enum class LifeStage(val label: String, val emoji: String, val tip: String) {
    PUPPY("Cachorro", "🍼", "Etapa de socialización y vacunas. Alimento específico para cachorros."),
    YOUNG("Joven", "⚡", "Mucha energía: ejercicio diario y entrenamiento básico."),
    ADULT("Adulto", "🦴", "Mantén su peso, paseos regulares y chequeo anual."),
    SENIOR("Senior", "🛋️", "Revisiones cada 6 meses, dieta adaptada y cuidado de articulaciones.")
}

data class AgeInput(val years: Int, val months: Int) {
    val totalYears: Double get() = years + months / 12.0
}

sealed class ParseResult {
    data class Success(val age: AgeInput) : ParseResult()
    data class Error(val message: String) : ParseResult()
}

data class DogAgeResult(
    val dogYears: Double,
    val humanYears: Double,
    val stage: LifeStage,
    /** Fracción de la vida esperada ya recorrida (0..1). */
    val lifeProgress: Float
)

object DogAgeCalculator {

    const val MAX_DOG_YEARS = 30
    const val CHART_MAX_YEARS = 20

    /** Valida los textos de años y meses (campos vacíos cuentan como 0). */
    fun parseAge(yearsText: String, monthsText: String): ParseResult {
        val y = yearsText.trim().ifEmpty { "0" }
        val m = monthsText.trim().ifEmpty { "0" }
        if (!y.all { it.isDigit() } || !m.all { it.isDigit() }) {
            return ParseResult.Error("Solo se permiten números enteros positivos.")
        }
        val years = y.toIntOrNull() ?: return ParseResult.Error("Número de años no válido.")
        val months = m.toIntOrNull() ?: return ParseResult.Error("Número de meses no válido.")
        if (months > 11) return ParseResult.Error("Los meses deben estar entre 0 y 11.")
        if (years > MAX_DOG_YEARS) return ParseResult.Error("La edad máxima soportada es $MAX_DOG_YEARS años.")
        if (years == 0 && months == 0) return ParseResult.Error("Ingresa la edad de tu perro (mayor a 0).")
        return ParseResult.Success(AgeInput(years, months))
    }

    /** Edad humana equivalente (en años) para una edad canina en años. */
    fun humanYears(dogYears: Double, size: DogSize, mode: CalcMode): Double {
        require(dogYears >= 0) { "La edad no puede ser negativa" }
        return when (mode) {
            CalcMode.SIMPLE -> dogYears * 7.0
            CalcMode.ADVANCED -> advanced(dogYears, size)
            CalcMode.SCIENTIFIC ->
                if (dogYears < 1.0) 31.0 * dogYears else 16.0 * ln(dogYears) + 31.0
        }
    }

    private fun advanced(a: Double, size: DogSize): Double = when {
        a <= 1.0 -> 15.0 * a
        a <= 2.0 -> 15.0 + 9.0 * (a - 1.0)
        else -> 24.0 + size.yearlyRate * (a - 2.0)
    }

    fun stageOf(dogYears: Double, size: DogSize): LifeStage = when {
        dogYears < 1.0 -> LifeStage.PUPPY
        dogYears < 3.0 -> LifeStage.YOUNG
        dogYears < size.seniorFrom -> LifeStage.ADULT
        else -> LifeStage.SENIOR
    }

    fun calculate(age: AgeInput, size: DogSize, mode: CalcMode): DogAgeResult {
        val dog = age.totalYears
        return DogAgeResult(
            dogYears = dog,
            humanYears = humanYears(dog, size, mode),
            stage = stageOf(dog, size),
            lifeProgress = min(1.0, dog / size.lifeExpectancy).toFloat()
        )
    }

    /** Puntos (edad canina, edad humana) para dibujar la curva. */
    fun curve(size: DogSize, mode: CalcMode, maxYears: Int = CHART_MAX_YEARS): List<Pair<Double, Double>> =
        (0..maxYears * 4).map { i ->
            val x = i / 4.0
            x to humanYears(x, size, mode)
        }
}
