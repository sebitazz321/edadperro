package com.optativa.dogage.logic

import java.text.Normalizer
import kotlin.math.roundToInt

/**
 * Chatbot "Pepe": asistente local basado en reglas (funciona sin internet ni claves de API).
 * Entiende frases como "mi perro tiene 5 años y 3 meses, es grande".
 */
class DogBot {

    private var lastSize: DogSize? = null

    companion object {
        val SUGGESTIONS = listOf(
            "Mi perro tiene 5 años",
            "¿Cómo se calcula?",
            "Cuidados de un perro senior",
            "¿Qué vacunas necesita?",
            "¿Cuánto vive un perro?"
        )

        fun normalize(text: String): String =
            Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
                .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun greeting(): String =
        "¡Guau! 🐾 Soy Pepe, tu asistente canino. Dime la edad de tu perro " +
            "(por ejemplo: \"tiene 4 años y 6 meses, es grande\") y te digo su edad humana. " +
            "También puedo hablar de cuidados, vacunas y alimentación."

    fun reply(input: String): String {
        val t = normalize(input)
        detectSize(t)?.let { lastSize = it }

        val years = Regex("(\\d+(?:[.,]\\d+)?)\\s*(?:anos|ano|a)\\b").find(t)
            ?.groupValues?.get(1)?.replace(',', '.')?.toDoubleOrNull()
        val months = Regex("(\\d+)\\s*(?:meses|mes|m)\\b").find(t)?.groupValues?.get(1)?.toIntOrNull()
        val bare = Regex("(?:tiene|edad|con)\\s+(\\d+(?:[.,]\\d+)?)\\b").find(t)
            ?.groupValues?.get(1)?.replace(',', '.')?.toDoubleOrNull()

        val dogYears = when {
            years != null || months != null -> (years ?: 0.0) + (months ?: 0) / 12.0
            bare != null -> bare
            else -> null
        }
        if (dogYears != null) return ageAnswer(dogYears)

        return when {
            has(t, "hola", "buenas", "buenos dias", "buenas tardes", "hey", "saludos") ->
                "¡Hola! 🐶 ¿Qué edad tiene tu perro? Puedes escribirme algo como \"tiene 7 años\"."
            has(t, "gracias", "genial", "perfecto") -> "¡De nada! 🐾 Cualquier otra duda, aquí estoy."
            has(t, "adios", "chao", "hasta luego", "bye") -> "¡Hasta pronto! Dale una caricia a tu peludo 🐕"
            has(t, "formula", "calcula", "como funciona", "metodo", "regla") ->
                "Uso tres métodos:\n" +
                    "• Simple: años × 7.\n" +
                    "• Avanzado: 1.er año = 15, 2.º = +9 y luego +${DogSize.SMALL.yearlyRate.toInt()} a " +
                    "+${DogSize.GIANT.yearlyRate.toInt()} por año según el tamaño.\n" +
                    "• Científico: 16 · ln(edad) + 31.\n" +
                    "Cambia de método en la pestaña Calculadora."
            has(t, "vacuna", "vacunar", "desparasit") ->
                "Calendario general: primeras vacunas a las 6–8 semanas, refuerzos cada 3–4 semanas hasta " +
                    "las 16 semanas, rabia a partir de los 3 meses y refuerzo anual. " +
                    "Tu veterinario ajusta el plan. 💉"
            has(t, "comida", "alimenta", "comer", "dieta", "croqueta", "pienso") ->
                "Elige alimento según la etapa: cachorro (3–4 comidas/día), adulto (2/día) y senior " +
                    "(con menos calorías y apoyo articular). Evita chocolate, uvas, cebolla y xilitol. 🥣"
            has(t, "ejercicio", "pasear", "paseo", "caminar", "jugar") ->
                "Un adulto necesita 30–120 min de actividad al día según raza y tamaño. " +
                    "Los senior prefieren paseos más cortos y frecuentes. 🦮"
            has(t, "senior", "viejo", "anciano", "mayor") ->
                "En la etapa senior: chequeos cada 6 meses, camas ortopédicas, dieta adaptada, " +
                    "control dental y paseos suaves. ${LifeStage.SENIOR.emoji}"
            has(t, "cachorro", "bebe") -> "${LifeStage.PUPPY.emoji} ${LifeStage.PUPPY.tip}"
            has(t, "cuanto vive", "esperanza", "vida", "longevidad") ->
                "Promedio aproximado: pequeños ${DogSize.SMALL.lifeExpectancy.toInt()} años, " +
                    "medianos ${DogSize.MEDIUM.lifeExpectancy.toInt()}, " +
                    "grandes ${DogSize.LARGE.lifeExpectancy.toInt()} y " +
                    "gigantes ${DogSize.GIANT.lifeExpectancy.toInt()}. Los grandes envejecen más rápido."
            has(t, "tamano", "raza", "peso") ->
                "Dime si es pequeño, mediano, grande o gigante y ajusto el cálculo. " +
                    "Ejemplo: \"tiene 6 años, es grande\"."
            else -> "No estoy seguro de entender 🤔. Prueba con \"mi perro tiene 5 años\", " +
                "\"¿qué vacunas necesita?\" o \"¿cómo se calcula?\"."
        }
    }

    private fun ageAnswer(dogYears: Double): String {
        if (dogYears <= 0.0 || dogYears > DogAgeCalculator.MAX_DOG_YEARS) {
            return "Esa edad no me cuadra 🧐. Dime una edad entre 1 mes y ${DogAgeCalculator.MAX_DOG_YEARS} años."
        }
        val size = lastSize ?: DogSize.MEDIUM
        val human = DogAgeCalculator.humanYears(dogYears, size, CalcMode.ADVANCED).roundToInt()
        val simple = DogAgeCalculator.humanYears(dogYears, size, CalcMode.SIMPLE).roundToInt()
        val stage = DogAgeCalculator.stageOf(dogYears, size)
        val sizeNote = if (lastSize == null) " (asumí tamaño mediano; dime el tamaño para afinar)" else ""
        return "${size.emoji} Un perro ${size.label.lowercase()} de ${formatAge(dogYears)} equivale a unos " +
            "$human años humanos$sizeNote.\nCon la regla clásica ×7 serían $simple.\n" +
            "Etapa: ${stage.emoji} ${stage.label}. ${stage.tip}"
    }

    private fun formatAge(dogYears: Double): String {
        val totalMonths = (dogYears * 12).roundToInt()
        val y = totalMonths / 12
        val m = totalMonths % 12
        return when {
            y == 0 -> "$m ${if (m == 1) "mes" else "meses"}"
            m == 0 -> "$y ${if (y == 1) "año" else "años"}"
            else -> "$y ${if (y == 1) "año" else "años"} y $m ${if (m == 1) "mes" else "meses"}"
        }
    }

    private fun detectSize(t: String): DogSize? = when {
        has(t, "gigante", "mastin", "gran danes", "san bernardo") -> DogSize.GIANT
        has(t, "grande", "labrador", "pastor", "golden", "husky", "rottweiler") -> DogSize.LARGE
        has(t, "pequeno", "chico", "chihuahua", "yorkshire", "mini", "toy", "pug", "caniche") -> DogSize.SMALL
        has(t, "mediano", "beagle", "bulldog", "border collie") -> DogSize.MEDIUM
        else -> null
    }

    private fun has(text: String, vararg keys: String) = keys.any { text.contains(it) }
}
