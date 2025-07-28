package com.petchat.android.data.remote.websocket

import com.petchat.android.domain.model.Message
import com.petchat.android.domain.model.ConnectionStatus
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.messageAdapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamAdapter.coroutines.CoroutinesStreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) {
    private val scarlet: PetChatWebSocketService by lazy {
        Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory(WEBSOCKET_URL))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()
            .create()
    }

    fun observeConnectionStatus(): Flow<ConnectionStatus> {
        return scarlet.observeWebSocketEvent().map { event ->
            when (event) {
                is WebSocket.Event.OnConnectionOpened<*> -> ConnectionStatus.CONNECTED
                is WebSocket.Event.OnConnectionClosed -> ConnectionStatus.DISCONNECTED
                is WebSocket.Event.OnConnectionClosing -> ConnectionStatus.DISCONNECTING
                is WebSocket.Event.OnConnectionFailed -> ConnectionStatus.FAILED
                else -> ConnectionStatus.UNKNOWN
            }
        }
    }

    fun observeMessages(): Flow<WebSocketMessage> {
        return scarlet.observeMessages()
    }

    fun sendMessage(message: Message) {
        scarlet.sendMessage(
            WebSocketMessage.ChatMessage(
                messageId = message.id,
                chatId = message.chatId,
                content = message.content,
                senderId = message.senderId,
                timestamp = message.timestamp
            )
        )
    }

    fun joinChat(chatId: String) {
        scarlet.sendMessage(
            WebSocketMessage.JoinChat(chatId = chatId)
        )
    }

    fun leaveChat(chatId: String) {
        scarlet.sendMessage(
            WebSocketMessage.LeaveChat(chatId = chatId)
        )
    }

    fun sendTypingIndicator(chatId: String, isTyping: Boolean) {
        scarlet.sendMessage(
            WebSocketMessage.TypingIndicator(
                chatId = chatId,
                userId = getCurrentUserId(),
                isTyping = isTyping
            )
        )
    }

    companion object {
        private const val WEBSOCKET_URL = "wss://api.petchat.com/ws"
    }
}

// WebSocket服务接口
interface PetChatWebSocketService {
    @Receive
    fun observeWebSocketEvent(): Flow<WebSocket.Event>
    
    @Receive
    fun observeMessages(): Flow<WebSocketMessage>
    
    @Send
    fun sendMessage(message: WebSocketMessage)
}

// WebSocket消息类型
sealed class WebSocketMessage {
    data class ChatMessage(
        val messageId: String,
        val chatId: String,
        val content: String,
        val senderId: String,
        val timestamp: Long
    ) : WebSocketMessage()
    
    data class JoinChat(
        val chatId: String
    ) : WebSocketMessage()
    
    data class LeaveChat(
        val chatId: String
    ) : WebSocketMessage()
    
    data class TypingIndicator(
        val chatId: String,
        val userId: String,
        val isTyping: Boolean
    ) : WebSocketMessage()
    
    data class MessageStatus(
        val messageId: String,
        val status: String
    ) : WebSocketMessage()
    
    data class Error(
        val code: Int,
        val message: String
    ) : WebSocketMessage()
}

// WebSocket重连管理器
class WebSocketReconnectManager @Inject constructor(
    private val webSocketManager: ChatWebSocketManager
) {
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMs = listOf(1000L, 2000L, 4000L, 8000L, 16000L)
    
    suspend fun handleReconnect() {
        webSocketManager.observeConnectionStatus().collect { status ->
            when (status) {
                ConnectionStatus.FAILED, ConnectionStatus.DISCONNECTED -> {
                    if (reconnectAttempts < maxReconnectAttempts) {
                        delay(reconnectDelayMs[reconnectAttempts])
                        reconnectAttempts++
                        // 触发重连
                        reconnect()
                    }
                }
                ConnectionStatus.CONNECTED -> {
                    reconnectAttempts = 0
                }
                else -> {}
            }
        }
    }
    
    private suspend fun reconnect() {
        // 实现重连逻辑
    }
}