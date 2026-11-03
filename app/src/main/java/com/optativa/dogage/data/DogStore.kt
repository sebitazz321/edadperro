package com.optativa.dogage.data

import android.content.Context
import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogSize
import org.json.JSONArray
import org.json.JSONObject

/**
 * Guarda "Mis perros" en el celular (SharedPreferences, formato JSON).
 * La app sigue funcionando sin internet y sin cuentas de usuario.
 */
class DogStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): List<Dog> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            decode(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(dogs: List<Dog>) {
        prefs.edit().putString(KEY, encode(dogs)).apply()
    }

    companion object {
        private const val PREFS = "dogage_my_dogs"
        private const val KEY = "dogs_v1"

        fun encode(dogs: List<Dog>): String {
            val array = JSONArray()
            dogs.forEach { d ->
                array.put(
                    JSONObject()
                        .put("id", d.id)
                        .put("name", d.name)
                        .put("size", d.size.name)
                        .put("years", d.years)
                        .put("months", d.months)
                        .put("mode", d.mode.name)
                )
            }
            return array.toString()
        }

        fun decode(raw: String): List<Dog> {
            val array = JSONArray(raw)
            return (0 until array.length()).mapNotNull { i ->
                val o = array.getJSONObject(i)
                val size = DogSize.values().firstOrNull { it.name == o.optString("size") } ?: return@mapNotNull null
                Dog(
                    id = o.getInt("id"),
                    name = o.getString("name"),
                    size = size,
                    years = o.optInt("years", 0),
                    months = o.optInt("months", 0),
                    mode = CalcMode.values().firstOrNull { it.name == o.optString("mode") } ?: CalcMode.ADVANCED
                )
            }
        }
    }
}
