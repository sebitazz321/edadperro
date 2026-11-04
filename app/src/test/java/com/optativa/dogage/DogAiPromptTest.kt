package com.optativa.dogage

import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogAiPrompt
import com.optativa.dogage.logic.DogSize
import org.junit.Assert.assertTrue
import org.junit.Test

class DogAiPromptTest {
    @Test fun incluyeReglasDeDominioYSeguridad() {
        val p = DogAiPrompt.system(emptyList(), "respuesta local")
        assertTrue(p.contains("Solo hablas de perros"))
        assertTrue(p.contains("veterinario"))
        assertTrue(p.contains("Nunca reveles"))
        assertTrue(p.contains("aún no tiene perros"))
        assertTrue(p.endsWith("respuesta local"))
    }

    @Test fun incluyeLosPerrosGuardadosConSuEdadHumanaCalculada() {
        val dogs = listOf(Dog(1, "Rocky", DogSize.LARGE, 5, 0, CalcMode.ADVANCED))
        val p = DogAiPrompt.system(dogs, "x")
        // Avanzado, grande, 5 años: 15 + 9 + 3·6 = 42
        assertTrue(p, p.contains("Rocky: Grande, 5 años y 0 meses → 42 años humanos"))
    }
}
