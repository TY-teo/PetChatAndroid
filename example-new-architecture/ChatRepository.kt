package com.petchat.android.data.repository

import com.petchat.android.data.local.dao.MessageDao
import com.petchat.android.data.local.entity.MessageEntity
import com.petchat.android.data.mapper.toEntity
import com.petchat.android.data.mapper.toModel
import com.petchat.android.data.remote.api.ChatApi
import com.petchat.android.data.remote.dto.SendMessageRequest
import com.petchat.android.data.remote.websocket.ChatWebSocketManager
import com.petchat.android.domain.model.Message
import com.petchat.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val messageDao: MessageDao,
    private val webSocketManager: ChatWebSocketManager
) : ChatRepository {

    override suspend fun sendMessage(chatId: String, content: String): Result<Message> {
        return try {
            // 创建本地消息
            val localMessage = Message(
                id = generateMessageId(),
                chatId = chatId,
                content = content,
                senderId = getCurrentUserId(),
                senderType = Message.SenderType.USER,
                timestamp = System.currentTimeMillis(),
                status = Message.Status.SENDING
            )

            // 保存到本地数据库
            messageDao.insertMessage(localMessage.toEntity())

            // 发送到服务器
            val response = chatApi.sendMessage(
                SendMessageRequest(
                    chatId = chatId,
                    content = content,
                    senderId = getCurrentUserId()
                )
            )

            // 更新本地消息状态
            val sentMessage = localMessage.copy(
                id = response.id,
                status = Message.Status.SENT
            )
            messageDao.updateMessage(sentMessage.toEntity())

            // 通过WebSocket通知
            webSocketManager.emitMessage(sentMessage)

            Result.success(sentMessage)
        } catch (e: Exception) {
            // 更新失败状态
            messageDao.updateMessageStatus(messageId, Message.Status.FAILED)
            Result.failure(e)
        }
    }

    override fun observeMessages(chatId: String): Flow<List<Message>> {
        return messageDao.observeMessages(chatId)
            .map { entities ->
                entities.map { it.toModel() }
            }
    }

    override suspend fun getMessages(chatId: String, limit: Int, offset: Int): List<Message> {
        // 尝试从网络获取最新消息
        try {
            val response = chatApi.getMessages(chatId, limit, offset)
            val entities = response.messages.map { it.toEntity() }
            messageDao.insertMessages(entities)
        } catch (e: Exception) {
            // 网络失败，使用本地缓存
        }

        // 返回本地数据
        return messageDao.getMessages(chatId, limit, offset)
            .map { it.toModel() }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            chatApi.deleteMessage(messageId)
            messageDao.deleteMessage(messageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessagesAsRead(chatId: String): Result<Unit> {
        return try {
            chatApi.markAsRead(chatId)
            messageDao.markMessagesAsRead(chatId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(0..9999).random()}"
    }

    private fun getCurrentUserId(): String {
        // 从认证管理器获取当前用户ID
        return "user_123" // 示例
    }
}

// 缓存策略实现
class ChatCacheStrategy @Inject constructor(
    private val messageDao: MessageDao
) {
    companion object {
        private const val CACHE_VALIDITY_DURATION = 5 * 60 * 1000L // 5分钟
    }

    suspend fun getCachedMessages(chatId: String): List<MessageEntity>? {
        val messages = messageDao.getMessagesWithTimestamp(chatId)
        if (messages.isEmpty()) return null

        val oldestMessageTime = messages.minOf { it.cachedAt }
        val isExpired = System.currentTimeMillis() - oldestMessageTime > CACHE_VALIDITY_DURATION

        return if (isExpired) null else messages
    }

    suspend fun cacheMessages(messages: List<MessageEntity>) {
        val timestampedMessages = messages.map { 
            it.copy(cachedAt = System.currentTimeMillis())
        }
        messageDao.insertMessages(timestampedMessages)
    }
}