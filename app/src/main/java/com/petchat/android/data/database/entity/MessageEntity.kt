package com.petchat.android.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.petchat.android.models.MessageType
import com.petchat.android.models.PetEmotion

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val type: MessageType,
    val timestamp: Long,
    val isRead: Boolean,
    val mediaUrl: String?,
    val emotion: PetEmotion?,
    val chatId: String = "default" // 支持多个聊天会话
)