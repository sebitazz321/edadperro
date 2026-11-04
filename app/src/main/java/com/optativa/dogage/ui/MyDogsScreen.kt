package com.optativa.dogage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogOps
import com.optativa.dogage.logic.DogSize
import kotlin.math.roundToInt

/** Pestaña "Mis perros": lista de perros guardados con su edad humana. */
@Composable
fun MyDogsScreen(vm: DogsViewModel, modifier: Modifier = Modifier, onOpenCalculator: () -> Unit) {
    var creating by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Int?>(null) }
    val stats = DogOps.stats(vm.dogs)

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("🐶 Mis perros", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Guarda a tus peludos y consulta su edad humana cuando quieras.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
            if (vm.dogs.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatTile("🐾", "${stats.total}", "Perros", Modifier.weight(1f))
                        StatTile("🧑", "${stats.averageHumanYears}", "Edad humana prom.", Modifier.weight(1f))
                        StatTile("🛋️", "${stats.seniors}", "Senior", Modifier.weight(1f))
                    }
                }
                stats.oldest?.let { o ->
                    item {
                        Text(
                            "👑 El mayor de la manada: ${o.name} (${formatAge(o.years, o.months)})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🦴", fontSize = 48.sp)
                            Text("Aún no tienes perros guardados", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Toca «＋ Agregar» o guarda uno desde la Calculadora.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            items(vm.dogs, key = { it.id }) { dog ->
                DogCard(
                    dog = dog,
                    onCalculator = { vm.loadInCalculator(dog); onOpenCalculator() },
                    onEdit = { editingId = dog.id },
                    onDelete = { deletingId = dog.id },
                    onBirthday = { vm.addMonths(dog.id, it) }
                )
            }
        }
        FloatingActionButton(
            onClick = { creating = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("dogs_add")
        ) { Text("＋ Agregar", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 16.dp)) }
    }

    if (creating) {
        DogFormDialog(title = "Nuevo perro", initial = null, onDismiss = { creating = false }) { name, size, y, m, mode ->
            vm.saveDog(name, size, y, m, mode).also { if (it == null) creating = false }
        }
    }
    editingId?.let { id ->
        vm.dogs.firstOrNull { it.id == id }?.let { dog ->
            DogFormDialog(title = "Editar a ${dog.name}", initial = dog, onDismiss = { editingId = null }) { name, size, y, m, mode ->
                vm.saveDog(name, size, y, m, mode, editingId = id).also { if (it == null) editingId = null }
            }
        }
    }
    deletingId?.let { id ->
        val name = vm.dogs.firstOrNull { it.id == id }?.name ?: ""
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("¿Eliminar a $name?") },
            text = { Text("Se quitará de Mis perros. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteDog(id); deletingId = null }, modifier = Modifier.testTag("dogs_confirm_delete")) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deletingId = null }) { Text("Cancelar") } }
        )
    }
    DogsMessageDialog(vm)
}

@Composable
private fun DogCard(
    dog: Dog,
    onCalculator: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBirthday: (Int) -> Unit
) {
    val s = DogOps.summary(dog)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dog_${dog.id}")
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) { Text(dog.size.emoji, fontSize = 28.sp) }
                Column(Modifier.weight(1f)) {
                    Text(dog.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${dog.size.label} · ${formatAge(dog.years, dog.months)}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${s.result.stage.emoji} ${s.result.stage.label} · método ${dog.mode.label.lowercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${s.humanYearsRounded}", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text("años hum.", style = MaterialTheme.typography.labelSmall)
                }
            }
            LinearProgressIndicator(
                progress = { s.result.lifeProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )
            Text(
                "Vida recorrida: ${(s.result.lifeProgress * 100).roundToInt()}% de ~${dog.size.lifeExpectancy.roundToInt()} años",
                style = MaterialTheme.typography.labelSmall
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                OutlinedButton(onClick = { onBirthday(1) }, contentPadding = PaddingValues(horizontal = 10.dp)) { Text("+1 mes") }
                OutlinedButton(onClick = { onBirthday(12) }, contentPadding = PaddingValues(horizontal = 10.dp)) { Text("🎂 +1 año") }
                TextButton(onClick = onCalculator) { Text("🧮 Calcular") }
                TextButton(onClick = onEdit) { Text("Editar") }
                TextButton(onClick = onDelete, modifier = Modifier.testTag("dog_delete_${dog.id}").semantics { contentDescription = "Eliminar a ${dog.name}" }) { Text("🗑️") }
            }
        }
    }
}

/**
 * Formulario para crear/editar un perro. [onSave] devuelve el error de validación o null si se guardó.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogFormDialog(
    title: String,
    initial: Dog?,
    onDismiss: () -> Unit,
    presetSize: DogSize = DogSize.MEDIUM,
    presetYears: String = "",
    presetMonths: String = "0",
    presetMode: CalcMode = CalcMode.ADVANCED,
    onSave: (String, DogSize, String, String, CalcMode) -> String?
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var size by remember { mutableStateOf(initial?.size ?: presetSize) }
    var years by remember { mutableStateOf(initial?.years?.toString() ?: presetYears) }
    var months by remember { mutableStateOf(initial?.months?.toString() ?: presetMonths) }
    val mode = initial?.mode ?: presetMode
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20); error = null },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dog_name")
                )
                Text("Tamaño", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    DogSize.values().forEach { s ->
                        FilterChip(selected = size == s, onClick = { size = s }, label = { Text("${s.emoji} ${s.label}", fontSize = 12.sp) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = years,
                        onValueChange = { years = it.filter(Char::isDigit).take(2); error = null },
                        label = { Text("Años") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dog_years")
                    )
                    OutlinedTextField(
                        value = months,
                        onValueChange = { months = it.filter(Char::isDigit).take(2); error = null },
                        label = { Text("Meses") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dog_months")
                    )
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("dog_error")) }
            }
        },
        confirmButton = {
            Button(onClick = { error = onSave(name, size, years, months, mode) }, modifier = Modifier.testTag("dog_save")) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun DogsMessageDialog(vm: DogsViewModel) {
    vm.message?.let { msg ->
        AlertDialog(
            onDismissRequest = { vm.dismissMessage() },
            title = { Text("Mis perros") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { vm.dismissMessage() }) { Text("Entendido") } }
        )
    }
}

@Composable
private fun StatTile(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
    }
}

fun formatAge(years: Int, months: Int): String = when {
    years == 0 -> "$months ${if (months == 1) "mes" else "meses"}"
    months == 0 -> "$years ${if (years == 1) "año" else "años"}"
    else -> "$years ${if (years == 1) "año" else "años"} y $months ${if (months == 1) "mes" else "meses"}"
}
