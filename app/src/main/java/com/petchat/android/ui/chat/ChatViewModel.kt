package com.petchat.android.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petchat.android.data.repository.ChatRepository
import com.petchat.android.data.repository.ChatRepositoryImpl
import com.petchat.android.models.Message
import com.petchat.android.models.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    
    private val repository: ChatRepository = ChatRepositoryImpl()
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        // 监听消息变化
        viewModelScope.launch {
            repository.getMessages().collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
        
        // 添加欢迎消息
        viewModelScope.launch {
            val welcomeMessage = Message(
                content = "主人，欢迎使用PetChat！我是你的宠物小白~ 有什么想对我说的吗？",
                type = MessageType.PET
            )
            repository.sendMessage(welcomeMessage)
        }
    }
    
    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            val message = Message(
                content = content,
                type = MessageType.USER
            )
            repository.sendMessage(message)
        }
    }
    
    fun toggleVoiceMode() {
        _uiState.value = _uiState.value.copy(
            isVoiceMode = !_uiState.value.isVoiceMode
        )
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isVoiceMode: Boolean = false,
    val isLoading: Boolean = false
)