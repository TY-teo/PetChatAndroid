package com.petchat.android.models

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val mediaUrl: String? = null,
    val emotion: PetEmotion? = null
) {
    companion object {
        const val TYPE_USER = 0
        const val TYPE_PET = 1
    }
}

enum class MessageType {
    USER,
    PET
}

enum class PetEmotion {
    HAPPY,      // 开心
    EXCITED,    // 兴奋
    SAD,        // 伤心
    HUNGRY,     // 饥饿
    TIRED,      // 疲倦
    PLAYFUL,    // 想玩
    CURIOUS,    // 好奇
    NORMAL      // 正常
}