package com.petchat.android.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petchat.android.domain.model.Message
import com.petchat.android.domain.model.Pet
import com.petchat.android.domain.usecase.chat.ObserveMessagesUseCase
import com.petchat.android.domain.usecase.chat.SendMessageUseCase
import com.petchat.android.domain.usecase.pet.GetCurrentPetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getCurrentPetUseCase: GetCurrentPetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ChatUiEvent>()
    val uiEvent: SharedFlow<ChatUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadCurrentPet()
        observeMessages()
    }

    private fun loadCurrentPet() {
        viewModelScope.launch {
            getCurrentPetUseCase()
                .onSuccess { pet ->
                    _uiState.update { it.copy(currentPet = pet) }
                }
                .onFailure { error ->
                    _uiEvent.emit(ChatUiEvent.ShowError(error.message ?: "Failed to load pet"))
                }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeMessagesUseCase(chatId = "default_chat")
                .catch { error ->
                    _uiEvent.emit(ChatUiEvent.ShowError(error.message ?: "Failed to load messages"))
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    fun onSendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            sendMessageUseCase(
                chatId = "default_chat",
                content = text
            ).onSuccess { message ->
                _uiState.update { it.copy(isSending = false, inputText = "") }
                _uiEvent.emit(ChatUiEvent.MessageSent)
            }.onFailure { error ->
                _uiState.update { it.copy(isSending = false) }
                _uiEvent.emit(ChatUiEvent.ShowError(error.message ?: "Failed to send message"))
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onRetryClicked() {
        observeMessages()
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val currentPet: Pet? = null,
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ChatUiEvent {
    object MessageSent : ChatUiEvent()
    data class ShowError(val message: String) : ChatUiEvent()
    data class ScrollToMessage(val messageId: String) : ChatUiEvent()
}