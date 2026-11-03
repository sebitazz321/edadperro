package com.optativa.dogage.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.optativa.dogage.logic.DogBot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val fromUser: Boolean)

class ChatViewModel : ViewModel() {
    private val bot = DogBot()

    val messages = mutableStateListOf(ChatMessage(bot.greeting(), fromUser = false))

    var typing by mutableStateOf(false)
        private set

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || typing) return
        messages.add(ChatMessage(clean, fromUser = true))
        typing = true
        viewModelScope.launch {
            delay(500) // pequeña pausa para simular que el bot "escribe"
            messages.add(ChatMessage(bot.reply(clean), fromUser = false))
            typing = false
        }
    }
}
