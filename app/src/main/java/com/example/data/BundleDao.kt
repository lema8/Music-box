package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BundleDao {
    @Query("SELECT * FROM sound_bundles ORDER BY id DESC")
    fun getAllBundles(): Flow<List<SoundBundleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBundle(bundle: SoundBundleEntity)

    @Update
    suspend fun updateBundle(bundle: SoundBundleEntity)

    @Query("DELETE FROM sound_bundles WHERE id = :id")
    suspend fun deleteBundleById(id: String)
}
