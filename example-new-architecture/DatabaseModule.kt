package com.petchat.android.core.database.di

import android.content.Context
import androidx.room.Room
import com.petchat.android.core.database.PetChatDatabase
import com.petchat.android.core.database.dao.*
import com.petchat.android.core.database.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PetChatDatabase {
        // 生成数据库密码
        val passphrase = SQLiteDatabase.getBytes("PetChat2024!@#".toCharArray())
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            PetChatDatabase::class.java,
            "petchat.db"
        )
        .openHelperFactory(factory) // 数据库加密
        .addMigrations(MIGRATION_1_2) // 数据库迁移
        .fallbackToDestructiveMigration() // 迁移失败时重建数据库
        .build()
    }

    @Provides
    fun provideMessageDao(database: PetChatDatabase): MessageDao = database.messageDao()

    @Provides
    fun providePetDao(database: PetChatDatabase): PetDao = database.petDao()

    @Provides
    fun provideUserDao(database: PetChatDatabase): UserDao = database.userDao()

    @Provides
    fun provideChatDao(database: PetChatDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideMomentDao(database: PetChatDatabase): MomentDao = database.momentDao()
}

// 数据库定义
@Database(
    entities = [
        MessageEntity::class,
        PetEntity::class,
        UserEntity::class,
        ChatEntity::class,
        MomentEntity::class,
        LocationEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PetChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun petDao(): PetDao
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun momentDao(): MomentDao
    abstract fun locationDao(): LocationDao
}

// 类型转换器
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromMessageStatus(status: Message.Status): String {
        return status.name
    }

    @TypeConverter
    fun toMessageStatus(status: String): Message.Status {
        return Message.Status.valueOf(status)
    }
}

// 消息实体
@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["chat_id", "timestamp"]),
        Index(value = ["sender_id"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "chat_id")
    val chatId: String,
    
    val content: String,
    
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    
    @ColumnInfo(name = "sender_type")
    val senderType: String,
    
    val timestamp: Long,
    
    val status: Message.Status,
    
    @Embedded
    val media: MediaInfo? = null,
    
    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,
    
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)

// 媒体信息
data class MediaInfo(
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val size: Long? = null,
    val duration: Int? = null // 视频时长（秒）
)

enum class MediaType {
    IMAGE, VIDEO, AUDIO, FILE
}

// 消息DAO
@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessages(chatId: String, limit: Int, offset: Int): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY timestamp DESC")
    fun observeMessages(chatId: String): Flow<List<MessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)
    
    @Update
    suspend fun updateMessage(message: MessageEntity)
    
    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: Message.Status)
    
    @Query("UPDATE messages SET is_read = 1 WHERE chat_id = :chatId AND is_read = 0")
    suspend fun markMessagesAsRead(chatId: String)
    
    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
    
    @Query("SELECT * FROM messages WHERE chat_id = :chatId AND cached_at > :timestamp")
    suspend fun getMessagesWithTimestamp(chatId: String, timestamp: Long = 0): List<MessageEntity>
}