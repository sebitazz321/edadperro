package com.optativa.dogage.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.optativa.dogage.BuildConfig
import com.optativa.dogage.data.GeminiClient
import com.optativa.dogage.logic.AiErrorMapper
import com.optativa.dogage.logic.AiHistory
import com.optativa.dogage.logic.AiInputGuard
import com.optativa.dogage.logic.AiRateLimiter
import com.optativa.dogage.logic.AiResponseGuard
import com.optativa.dogage.logic.Dog
import com.optativa.dogage.logic.DogAiPrompt
import com.optativa.dogage.logic.DogBot
import com.optativa.dogage.logic.InputCheck
import com.optativa.dogage.logic.MsgSource
import com.optativa.dogage.logic.Turn
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val source: MsgSource) {
    val fromUser: Boolean get() = source == MsgSource.USER
}

/**
 * Agente "Pepe" (híbrido):
 *  1. Valida la entrada (longitud, caracteres, intentos de manipular las instrucciones).
 *  2. El motor local [DogBot] calcula la respuesta exacta.
 *  3. Gemini redacta la respuesta final con esos datos + los perros guardados.
 *  4. Si la IA no está configurada o falla → respuesta local con aviso.
 */
class ChatViewModel : ViewModel() {
    private val bot = DogBot()
    private val ai = GeminiClient(BuildConfig.GEMINI_API_KEY)
    private val limiter = AiRateLimiter()

    val aiEnabled: Boolean get() = ai.isConfigured

    val messages = mutableStateListOf(ChatMessage(bot.greeting(), MsgSource.LOCAL))

    var typing by mutableStateOf(false)
        private set

    fun send(text: String, dogs: List<Dog> = emptyList()) {
        if (typing) return
        val clean = when (val check = AiInputGuard.check(text)) {
            is InputCheck.Ok -> check.text
            is InputCheck.Rejected -> {
                if (text.isNotBlank()) messages.add(ChatMessage(text.trim().take(80), MsgSource.USER))
                messages.add(ChatMessage(check.reason, MsgSource.LOCAL))
                return
            }
        }
        val history = AiHistory.trim(messages.map { Turn(if (it.fromUser) Turn.USER else Turn.MODEL, it.text) })
        messages.add(ChatMessage(clean, MsgSource.USER))
        typing = true
        viewModelScope.launch {
            val local = bot.reply(clean)
            val reply = when {
                !ai.isConfigured -> ChatMessage(local, MsgSource.LOCAL)
                !limiter.tryAcquire() -> ChatMessage("$local\n\n${AiErrorMapper.fallbackNote(AiErrorMapper.RATE_LIMITED_LOCAL)}", MsgSource.LOCAL)
                else -> when (val r = ai.generate(DogAiPrompt.system(dogs, local), history, clean)) {
                    is GeminiClient.Result.Ok -> AiResponseGuard.clean(r.text)?.let { ChatMessage(it, MsgSource.AI) }
                        ?: ChatMessage("$local\n\n${AiErrorMapper.fallbackNote(AiErrorMapper.BAD_RESPONSE)}", MsgSource.LOCAL)
                    is GeminiClient.Result.Fail -> ChatMessage("$local\n\n${AiErrorMapper.fallbackNote(r.reason)}", MsgSource.LOCAL)
                }
            }
            messages.add(reply)
            typing = false
        }
    }
}
