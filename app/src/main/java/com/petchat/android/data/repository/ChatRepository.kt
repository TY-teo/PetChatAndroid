package com.petchat.android.data.repository

import com.petchat.android.models.Message
import com.petchat.android.models.MessageType
import com.petchat.android.models.PetEmotion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlin.random.Random

interface ChatRepository {
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(message: Message)
    suspend fun clearMessages()
}

class ChatRepositoryImpl : ChatRepository {
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    override fun getMessages(): Flow<List<Message>> = _messages

    private val petResponses = mapOf(
        // 问候相关
        Regex("你好|hi|hello|嗨") to listOf(
            "汪汪！主人好~" to PetEmotion.HAPPY,
            "嗨！我好想你呀~" to PetEmotion.EXCITED,
            "主人，你回来啦！" to PetEmotion.HAPPY
        ),
        
        // 饮食相关
        Regex("吃|饿|食物|饭") to listOf(
            "汪汪！我想吃好吃的！" to PetEmotion.HUNGRY,
            "主人，是不是到饭点了？" to PetEmotion.HUNGRY,
            "我的小肚子咕咕叫了~" to PetEmotion.HUNGRY
        ),
        
        // 玩耍相关
        Regex("玩|游戏|球") to listOf(
            "太好了！我们一起玩吧~" to PetEmotion.PLAYFUL,
            "我最喜欢和主人玩了！" to PetEmotion.EXCITED,
            "汪汪！快把球丢过来！" to PetEmotion.PLAYFUL
        ),
        
        // 散步相关
        Regex("散步|出去|遛|走") to listOf(
            "太棒了！我们去散步吧！" to PetEmotion.EXCITED,
            "我想去公园玩！" to PetEmotion.PLAYFUL,
            "汪汪！出门啦出门啦！" to PetEmotion.EXCITED
        ),
        
        // 休息相关
        Regex("累|困|睡|休息") to listOf(
            "我也有点困了呢~" to PetEmotion.TIRED,
            "那我们一起休息吧~" to PetEmotion.TIRED,
            "打个哈欠，好困呀~" to PetEmotion.TIRED
        ),
        
        // 表扬相关
        Regex("乖|好狗|棒|厉害") to listOf(
            "汪汪~我最乖了！" to PetEmotion.HAPPY,
            "嘿嘿，被主人表扬了~" to PetEmotion.HAPPY,
            "我会更加努力的！" to PetEmotion.EXCITED
        ),
        
        // 关心相关
        Regex("怎么样|还好吗|开心") to listOf(
            "我很好！谢谢主人关心~" to PetEmotion.HAPPY,
            "有主人在我就很开心！" to PetEmotion.HAPPY,
            "今天心情特别好呢！" to PetEmotion.HAPPY
        )
    )

    private val defaultResponses = listOf(
        "汪汪！我很想你呢~" to PetEmotion.NORMAL,
        "主人，我刚才在想你！" to PetEmotion.HAPPY,
        "今天天气真好，想和你一起玩~" to PetEmotion.PLAYFUL,
        "我刚才看到一只小鸟，好想和你分享！" to PetEmotion.CURIOUS,
        "主人你回来了吗？我一直在等你~" to PetEmotion.NORMAL,
        "汪汪汪！我好开心！" to PetEmotion.HAPPY,
        "我想和你一起散步~" to PetEmotion.PLAYFUL,
        "主人，你今天过得怎么样？" to PetEmotion.CURIOUS,
        "我刚才睡了个好觉，梦到你了~" to PetEmotion.HAPPY,
        "汪！有好吃的吗？" to PetEmotion.HUNGRY
    )

    override suspend fun sendMessage(message: Message) {
        // 添加用户消息
        _messages.value = _messages.value + message
        
        // 模拟延迟
        delay(1000L + Random.nextLong(1500))
        
        // 生成宠物回复
        val petResponse = generatePetResponse(message.content)
        _messages.value = _messages.value + petResponse
    }

    private fun generatePetResponse(userMessage: String): Message {
        val lowerMessage = userMessage.lowercase()
        
        // 查找匹配的回复
        for ((pattern, responses) in petResponses) {
            if (pattern.containsMatchIn(lowerMessage)) {
                val (content, emotion) = responses.random()
                return Message(
                    content = content,
                    type = MessageType.PET,
                    emotion = emotion
                )
            }
        }
        
        // 使用默认回复
        val (content, emotion) = defaultResponses.random()
        return Message(
            content = content,
            type = MessageType.PET,
            emotion = emotion
        )
    }

    override suspend fun clearMessages() {
        _messages.value = emptyList()
    }
}