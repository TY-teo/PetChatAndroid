package com.petchat.android.data.database.dao

import androidx.room.*
import com.petchat.android.data.database.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    
    @Query("SELECT * FROM pets WHERE isActive = 1 LIMIT 1")
    fun getActivePet(): Flow<PetEntity?>
    
    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<PetEntity>>
    
    @Query("SELECT * FROM pets WHERE id = :petId")
    suspend fun getPetById(petId: String): PetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)
    
    @Update
    suspend fun updatePet(pet: PetEntity)
    
    @Delete
    suspend fun deletePet(pet: PetEntity)
    
    @Query("UPDATE pets SET isActive = 0")
    suspend fun deactivateAllPets()
    
    @Transaction
    suspend fun setActivePet(petId: String) {
        deactivateAllPets()
        val pet = getPetById(petId)
        pet?.let {
            updatePet(it.copy(isActive = true))
        }
    }
}