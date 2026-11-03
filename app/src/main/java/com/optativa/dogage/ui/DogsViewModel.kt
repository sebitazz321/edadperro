package com.optativa.dogage.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.optativa.dogage.data.DogStore
import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.DogAgeCalculator
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogOps
import com.optativa.dogage.logic.DogSize
import com.optativa.dogage.logic.DogValidator
import com.optativa.dogage.logic.ParseResult

/** Estado de "Mis perros", compartido entre la calculadora y la lista. */
class DogsViewModel(app: Application) : AndroidViewModel(app) {

    private val store = DogStore(app)

    var dogs by mutableStateOf(DogOps.sorted(store.load()))
        private set

    /** Mensaje corto para mostrar en un diálogo (guardado, eliminado, error). */
    var message by mutableStateOf<String?>(null)
        private set

    /** Perro que la calculadora debe cargar (se consume una sola vez). */
    var pendingLoad by mutableStateOf<Dog?>(null)
        private set

    fun dismissMessage() { message = null }
    fun consumeLoad() { pendingLoad = null }

    private fun updateDogs(newDogs: List<Dog>) {
        dogs = DogOps.sorted(newDogs)
        store.save(dogs)
    }

    /** Valida y guarda (nuevo si [editingId] es null). Devuelve el error o null si se guardó. */
    fun saveDog(
        name: String,
        size: DogSize,
        yearsText: String,
        monthsText: String,
        mode: CalcMode = CalcMode.ADVANCED,
        editingId: Int? = null
    ): String? {
        val error = DogValidator.validate(name, yearsText, monthsText, dogs, editingId)
        if (error != null) return error
        val age = (DogAgeCalculator.parseAge(yearsText, monthsText) as ParseResult.Success).age
        val dog = Dog(editingId ?: DogOps.nextId(dogs), name.trim(), size, age.years, age.months, mode)
        updateDogs(DogOps.upsert(dogs, dog))
        message = if (editingId == null) "🐾 «${dog.name}» se guardó en Mis perros." else "✅ «${dog.name}» actualizado."
        return null
    }

    fun deleteDog(id: Int) {
        val name = dogs.firstOrNull { it.id == id }?.name ?: return
        updateDogs(DogOps.delete(dogs, id))
        message = "🗑️ «$name» se eliminó de Mis perros."
    }

    fun addMonths(id: Int, months: Int) {
        val dog = dogs.firstOrNull { it.id == id } ?: return
        updateDogs(DogOps.upsert(dogs, DogOps.addMonths(dog, months)))
    }

    fun loadInCalculator(dog: Dog) { pendingLoad = dog }
}
