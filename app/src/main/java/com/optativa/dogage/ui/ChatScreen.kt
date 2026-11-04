package com.optativa.dogage.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.optativa.dogage.logic.AiConfig
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogBot
import com.optativa.dogage.logic.MsgSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(modifier: Modifier = Modifier, dogs: List<Dog> = emptyList(), vm: ChatViewModel = viewModel()) {
    var input by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(vm.messages.size, vm.typing) {
        val last = vm.messages.size + (if (vm.typing) 1 else 0) - 1
        if (last >= 0) listState.animateScrollToItem(last)
    }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Column {
                Text("🐶 Pepe, asistente canino", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                Text("Pregúntame por edad, cuidados y salud", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                Text(
                    if (vm.aiEnabled) "🤖 Asistente con inteligencia artificial (${AiConfig.PROVIDER}). Puede equivocarse: consulta al veterinario ante síntomas."
                    else "⚙️ Modo local sin IA (falta configurar GEMINI_API_KEY).",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 4.dp).testTag("ai_disclosure")
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            items(vm.messages) { msg -> Bubble(msg) }
            if (vm.typing) item { Bubble(ChatMessage("Pepe está escribiendo…", MsgSource.LOCAL)) }
        }

        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DogBot.SUGGESTIONS) { s ->
                AssistChip(onClick = { vm.send(s, dogs) }, label = { Text(s) })
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Escribe tu mensaje…") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                maxLines = 3,
                supportingText = if (input.length > AiConfig.MAX_INPUT_CHARS - 50) {
                    { Text("${input.length}/${AiConfig.MAX_INPUT_CHARS}") }
                } else null
            )
            Button(
                onClick = { vm.send(input, dogs); input = "" },
                enabled = input.isNotBlank() && !vm.typing,
                modifier = Modifier.testTag("chat_send")
            ) { Text("Enviar") }
        }
    }
}

@Composable
private fun Bubble(msg: ChatMessage) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.fromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp, topEnd = 18.dp,
                bottomStart = if (msg.fromUser) 18.dp else 4.dp,
                bottomEnd = if (msg.fromUser) 4.dp else 18.dp
            ),
            color = if (msg.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (msg.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = (if (msg.fromUser) "Tú: " else if (msg.source == MsgSource.AI) "Pepe, con IA: " else "Pepe: ") + msg.text
                }
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (!msg.fromUser) {
                    Text(
                        if (msg.source == MsgSource.AI) "🤖 IA · ${AiConfig.PROVIDER}" else "⚙️ Respuesta local",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(msg.text)
            }
        }
    }
}
