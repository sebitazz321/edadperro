package com.optativa.dogage

import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogOps
import com.optativa.dogage.logic.DogSize
import com.optativa.dogage.logic.DogValidator
import com.optativa.dogage.logic.LifeStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DogProfilesTest {

    private val firulais = Dog(1, "Firulais", DogSize.MEDIUM, 5, 0)
    private val luna = Dog(2, "Luna", DogSize.GIANT, 7, 6)
    private val toby = Dog(3, "Toby", DogSize.SMALL, 0, 4)
    private val all = listOf(firulais, luna, toby)

    // ---- validación
    @Test fun validator_acceptsValidDog() = assertNull(DogValidator.validate("Max", "3", "2", all))

    @Test fun validator_rejectsBadNames() {
        assertNotNull(DogValidator.validate("M", "3", "0", all))
        assertNotNull(DogValidator.validate("R2D2", "3", "0", all))
        assertNotNull(DogValidator.validate("A".repeat(21), "3", "0", all))
    }

    @Test fun validator_rejectsDuplicateIgnoringCaseAndAccents() {
        assertNotNull(DogValidator.validate("  LUNA ", "1", "0", all))
        assertNotNull(DogValidator.validate("Lúna", "1", "0", all))
        // editar al mismo perro conservando su nombre sí es válido
        assertNull(DogValidator.validate("Luna", "8", "0", all, editingId = 2))
    }

    @Test fun validator_reusesAgeRules() {
        assertNotNull(DogValidator.validate("Max", "0", "0", all))
        assertNotNull(DogValidator.validate("Max", "2", "12", all))
        assertNotNull(DogValidator.validate("Max", "31", "0", all))
    }

    @Test fun validator_limitsNumberOfDogs() {
        val many = (1..DogValidator.MAX_DOGS).map { Dog(it, "Perro" + "abcdefghijklmnopqrstuvwxyz"[it], DogSize.SMALL, 1, 0) }
        assertNotNull(DogValidator.validate("Nuevo", "1", "0", many))
        assertNull(DogValidator.validate("Nuevo", "1", "0", many, editingId = 1))
    }

    // ---- operaciones
    @Test fun ops_nextIdUpsertDelete() {
        assertEquals(4, DogOps.nextId(all))
        assertEquals(1, DogOps.nextId(emptyList()))
        val added = DogOps.upsert(all, Dog(4, "Max", DogSize.LARGE, 2, 0))
        assertEquals(4, added.size)
        val edited = DogOps.upsert(added, added.last().copy(years = 3))
        assertEquals(4, edited.size)
        assertEquals(3, edited.last().years)
        assertEquals(3, DogOps.delete(edited, 4).size)
    }

    @Test fun ops_addMonthsRollsOverAndClamps() {
        assertEquals(Dog(1, "Firulais", DogSize.MEDIUM, 6, 0), DogOps.addMonths(firulais, 12))
        assertEquals(1 to 0, DogOps.addMonths(Dog(9, "X", DogSize.SMALL, 0, 11), 1).let { it.years to it.months })
        val old = DogOps.addMonths(Dog(9, "X", DogSize.SMALL, 30, 11), 12)
        assertEquals(30 to 11, old.years to old.months)
        val min = DogOps.addMonths(Dog(9, "X", DogSize.SMALL, 0, 1), -5)
        assertEquals(0 to 1, min.years to min.months)
    }

    @Test fun ops_summaryUsesCalculator() {
        val s = DogOps.summary(firulais)            // avanzado mediano: 24 + 5*3 = 39
        assertEquals(39, s.humanYearsRounded)
        assertEquals(LifeStage.ADULT, s.result.stage)
        val simple = DogOps.summary(firulais.copy(mode = CalcMode.SIMPLE))
        assertEquals(35, simple.humanYearsRounded)
    }

    @Test fun ops_sortedAlphabeticallyIgnoringAccents() {
        val dogs = listOf(Dog(1, "Zeus", DogSize.SMALL, 1, 0), Dog(2, "Ámbar", DogSize.SMALL, 1, 0), Dog(3, "bruno", DogSize.SMALL, 1, 0))
        assertEquals(listOf("Ámbar", "bruno", "Zeus"), DogOps.sorted(dogs).map { it.name })
    }

    @Test fun ops_stats() {
        val st = DogOps.stats(all)
        assertEquals(3, st.total)
        assertEquals("Luna", st.oldest?.name)
        assertEquals(1, st.seniors)   // Luna gigante 7.5 años (senior desde 6)
        assertEquals(1, st.puppies)   // Toby 4 meses
        assertTrue(st.averageHumanYears > 0)
        val empty = DogOps.stats(emptyList())
        assertEquals(0, empty.total)
        assertNull(empty.oldest)
    }
}
