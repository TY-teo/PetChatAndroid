package com.petchat.android.data.database

import androidx.room.TypeConverter
import com.petchat.android.models.MessageType
import com.petchat.android.models.PetEmotion
import com.petchat.android.ui.pet.HealthStatus

class Converters {
    
    @TypeConverter
    fun fromMessageType(type: MessageType): String {
        return type.name
    }
    
    @TypeConverter
    fun toMessageType(type: String): MessageType {
        return MessageType.valueOf(type)
    }
    
    @TypeConverter
    fun fromPetEmotion(emotion: PetEmotion?): String? {
        return emotion?.name
    }
    
    @TypeConverter
    fun toPetEmotion(emotion: String?): PetEmotion? {
        return emotion?.let { PetEmotion.valueOf(it) }
    }
    
    @TypeConverter
    fun fromHealthStatus(status: HealthStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toHealthStatus(status: String): HealthStatus {
        return HealthStatus.valueOf(status)
    }
}