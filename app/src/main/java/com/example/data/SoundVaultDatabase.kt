package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TrackEntity::class, SoundBundleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SoundVaultDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun bundleDao(): BundleDao

    companion object {
        @Volatile
        private var INSTANCE: SoundVaultDatabase? = null

        fun getDatabase(context: Context): SoundVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundVaultDatabase::class.java,
                    "soundvault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
