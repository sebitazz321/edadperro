package com.optativa.dogage

import com.optativa.dogage.logic.AgeInput
import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.DogAgeCalculator
import com.optativa.dogage.logic.DogSize
import com.optativa.dogage.logic.LifeStage
import com.optativa.dogage.logic.ParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DogAgeCalculatorTest {

    @Test
    fun simpleMode_multipliesBySeven() {
        assertEquals(35.0, DogAgeCalculator.humanYears(5.0, DogSize.MEDIUM, CalcMode.SIMPLE), 0.001)
    }

    @Test
    fun advancedMode_firstTwoYears() {
        assertEquals(15.0, DogAgeCalculator.humanYears(1.0, DogSize.SMALL, CalcMode.ADVANCED), 0.001)
        assertEquals(24.0, DogAgeCalculator.humanYears(2.0, DogSize.GIANT, CalcMode.ADVANCED), 0.001)
    }

    @Test
    fun advancedMode_dependsOnSize() {
        val small = DogAgeCalculator.humanYears(10.0, DogSize.SMALL, CalcMode.ADVANCED)   // 24 + 4*8 = 56
        val giant = DogAgeCalculator.humanYears(10.0, DogSize.GIANT, CalcMode.ADVANCED)   // 24 + 7*8 = 80
        assertEquals(56.0, small, 0.001)
        assertEquals(80.0, giant, 0.001)
        assertTrue(giant > small)
    }

    @Test
    fun scientificMode_matchesFormula() {
        // 16 * ln(10) + 31 = 67.84
        assertEquals(67.84, DogAgeCalculator.humanYears(10.0, DogSize.MEDIUM, CalcMode.SCIENTIFIC), 0.01)
    }

    @Test
    fun curve_isMonotonic() {
        CalcMode.values().forEach { mode ->
            val ys = DogAgeCalculator.curve(DogSize.LARGE, mode).map { it.second }
            assertTrue("curva no creciente en $mode", ys.zipWithNext().all { (a, b) -> b >= a })
        }
    }

    @Test
    fun parse_valid() {
        val r = DogAgeCalculator.parseAge("4", "6") as ParseResult.Success
        assertEquals(4.5, r.age.totalYears, 0.001)
    }

    @Test
    fun parse_emptyMonthsCountsAsZero() {
        val r = DogAgeCalculator.parseAge("3", "") as ParseResult.Success
        assertEquals(AgeInput(3, 0), r.age)
    }

    @Test
    fun parse_rejectsInvalid() {
        assertTrue(DogAgeCalculator.parseAge("0", "0") is ParseResult.Error)
        assertTrue(DogAgeCalculator.parseAge("2", "12") is ParseResult.Error)
        assertTrue(DogAgeCalculator.parseAge("31", "0") is ParseResult.Error)
        assertTrue(DogAgeCalculator.parseAge("-1", "0") is ParseResult.Error)
        assertTrue(DogAgeCalculator.parseAge("a", "0") is ParseResult.Error)
    }

    @Test
    fun lifeStage_bySize() {
        assertEquals(LifeStage.PUPPY, DogAgeCalculator.stageOf(0.5, DogSize.MEDIUM))
        assertEquals(LifeStage.YOUNG, DogAgeCalculator.stageOf(2.0, DogSize.MEDIUM))
        assertEquals(LifeStage.ADULT, DogAgeCalculator.stageOf(7.0, DogSize.SMALL))
        assertEquals(LifeStage.SENIOR, DogAgeCalculator.stageOf(7.0, DogSize.GIANT))
    }

    @Test
    fun lifeProgress_isCapped() {
        val r = DogAgeCalculator.calculate(AgeInput(25, 0), DogSize.GIANT, CalcMode.ADVANCED)
        assertEquals(1f, r.lifeProgress, 0.0001f)
    }
}
