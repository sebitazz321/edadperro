package com.optativa.dogage.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.optativa.dogage.logic.AgeInput
import com.optativa.dogage.logic.CalcMode
import com.optativa.dogage.logic.DogAgeCalculator
import com.optativa.dogage.logic.DogSize
import com.optativa.dogage.logic.ParseResult
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(modifier: Modifier = Modifier, dogsVm: DogsViewModel? = null) {
    var size by rememberSaveable { mutableStateOf(DogSize.MEDIUM) }
    var mode by rememberSaveable { mutableStateOf(CalcMode.ADVANCED) }
    var yearsText by rememberSaveable { mutableStateOf("3") }
    var monthsText by rememberSaveable { mutableStateOf("0") }
    var loadedName by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by rememberSaveable { mutableStateOf(false) }

    // Cargar un perro elegido en "Mis perros"
    val pending = dogsVm?.pendingLoad
    LaunchedEffect(pending) {
        if (pending != null) {
            size = pending.size
            mode = pending.mode
            yearsText = pending.years.toString()
            monthsText = pending.months.toString()
            loadedName = pending.name
            dogsVm?.consumeLoad()
        }
    }

    val parsed = DogAgeCalculator.parseAge(yearsText, monthsText)
    val age: AgeInput? = (parsed as? ParseResult.Success)?.age
    val error: String? = (parsed as? ParseResult.Error)?.message

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text("🐾 Edad de tu perro", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    "Descubre cuántos años humanos tiene tu mejor amigo",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }

        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // --- Datos del perro ---
            SectionCard(title = loadedName?.let { "Datos de $it" } ?: "Datos de tu perro") {
                Text("Tamaño", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    DogSize.values().forEach { s ->
                        FilterChip(
                            selected = size == s,
                            onClick = { size = s; loadedName = null },
                            label = { Text("${s.emoji} ${s.label}", maxLines = 1, fontSize = 12.sp) },
                            modifier = Modifier.testTag("size_${s.name}")
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Edad", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = yearsText,
                        onValueChange = { yearsText = it.filter(Char::isDigit).take(2); loadedName = null },
                        label = { Text("Años") },
                        singleLine = true,
                        isError = error != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_years")
                    )
                    OutlinedTextField(
                        value = monthsText,
                        onValueChange = { monthsText = it.filter(Char::isDigit).take(2); loadedName = null },
                        label = { Text("Meses") },
                        singleLine = true,
                        isError = error != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_months")
                    )
                }
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(4.dp))
                Text("Método de cálculo", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    CalcMode.values().forEach { m ->
                        FilterChip(
                            selected = mode == m,
                            onClick = { mode = m },
                            label = { Text(m.label) },
                            modifier = Modifier.testTag("mode_${m.name}")
                        )
                    }
                }
                Text(mode.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            // --- Resultado ---
            if (age != null) {
                val result = DogAgeCalculator.calculate(age, size, mode)
                val animated by animateFloatAsState(targetValue = result.humanYears.toFloat(), label = "human")
                val progress by animateFloatAsState(targetValue = result.lifeProgress, label = "life")

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(size.emoji, fontSize = 56.sp)
                        Text(
                            "${animated.roundToInt()}",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("human_age")
                        )
                        Text("años humanos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("${result.stage.emoji} ${result.stage.label}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            result.stage.tip,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Vida recorrida: ${(progress * 100).roundToInt()}% de ~${size.lifeExpectancy.roundToInt()} años",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .height(10.dp)
                                .clip(CircleShape)
                        )
                        if (dogsVm != null && loadedName == null) {
                            Spacer(Modifier.height(12.dp))
                            OutlinedButton(onClick = { saving = true }, modifier = Modifier.testTag("save_dog")) {
                                Text("💾 Guardar en Mis perros")
                            }
                        }
                    }
                }

                SectionCard(title = "Perro vs. humano") {
                    AgeChart(size = size, mode = mode, dogYears = result.dogYears, humanYears = result.humanYears)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (saving && dogsVm != null) {
        DogFormDialog(
            title = "Guardar en Mis perros",
            initial = null,
            onDismiss = { saving = false },
            presetSize = size,
            presetYears = yearsText,
            presetMonths = monthsText,
            presetMode = mode
        ) { name, s, y, m, md ->
            dogsVm.saveDog(name, s, y, m, md).also {
                if (it == null) {
                    saving = false
                    loadedName = name.trim()
                }
            }
        }
    }
    if (dogsVm != null) DogsMessageDialog(dogsVm)
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

/** Gráfica de la curva de equivalencia con el punto actual resaltado. */
@Composable
private fun AgeChart(size: DogSize, mode: CalcMode, dogYears: Double, humanYears: Double) {
    val points = DogAgeCalculator.curve(size, mode)
    val maxHuman = (points.maxOf { it.second } * 1.1).coerceAtLeast(20.0)
    val maxX = DogAgeCalculator.CHART_MAX_YEARS.toDouble()
    val lineColor = MaterialTheme.colorScheme.primary
    val dotColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f).toArgb()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val left = 40.dp.toPx()
        val bottom = 24.dp.toPx()
        val w = this.size.width - left - 8.dp.toPx()
        val h = this.size.height - bottom - 8.dp.toPx()
        fun px(x: Double) = left + (x / maxX * w).toFloat()
        fun py(y: Double) = 8.dp.toPx() + (h - (y / maxHuman * h)).toFloat()

        val paint = android.graphics.Paint().apply {
            color = labelColor
            textSize = 10.sp.toPx()
            isAntiAlias = true
        }
        // cuadrícula + etiquetas
        for (i in 0..4) {
            val yv = maxHuman * i / 4
            drawLine(gridColor, Offset(left, py(yv)), Offset(left + w, py(yv)), 1.dp.toPx())
            drawContext.canvas.nativeCanvas.drawText("${yv.roundToInt()}", 4.dp.toPx(), py(yv) + 4.dp.toPx(), paint)
        }
        for (x in 0..DogAgeCalculator.CHART_MAX_YEARS step 5) {
            drawContext.canvas.nativeCanvas.drawText("$x", px(x.toDouble()) - 4.dp.toPx(), this.size.height - 6.dp.toPx(), paint)
        }
        // curva
        val path = Path()
        points.forEachIndexed { i, (x, y) ->
            if (i == 0) path.moveTo(px(x), py(y)) else path.lineTo(px(x), py(y))
        }
        drawPath(path, lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        // punto actual
        if (dogYears <= maxX) {
            val c = Offset(px(dogYears), py(humanYears))
            drawCircle(dotColor.copy(alpha = 0.25f), radius = 12.dp.toPx(), center = c)
            drawCircle(dotColor, radius = 6.dp.toPx(), center = c)
        }
    }
    Text(
        "Eje X: edad del perro (años) · Eje Y: edad humana",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}
