package com.petchat.android.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.petchat.android.ui.pet.HealthStatus

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val breed: String,
    val age: Int,
    val height: String,
    val weight: String,
    val birthday: String,
    val avatar: String,
    val healthStatus: HealthStatus,
    val isActive: Boolean = true // 当前激活的宠物
)