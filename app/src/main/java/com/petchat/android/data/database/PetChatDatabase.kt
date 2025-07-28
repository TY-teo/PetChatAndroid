package com.petchat.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.petchat.android.data.database.dao.MessageDao
import com.petchat.android.data.database.dao.PetDao
import com.petchat.android.data.database.entity.MessageEntity
import com.petchat.android.data.database.entity.PetEntity

@Database(
    entities = [
        MessageEntity::class,
        PetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PetChatDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao
    abstract fun petDao(): PetDao
    
    companion object {
        @Volatile
        private var INSTANCE: PetChatDatabase? = null
        
        fun getInstance(context: Context): PetChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetChatDatabase::class.java,
                    "petchat_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}