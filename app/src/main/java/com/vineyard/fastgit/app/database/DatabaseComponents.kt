package com.vineyard.fastgit.app.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cache_entries")
data class CacheEntity(
    @PrimaryKey val key: String,
    val valueJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface CacheDao {
    @Query("SELECT * FROM cache_entries WHERE key = :key LIMIT 1")
    suspend fun getCache(key: String): CacheEntity?

    @Query("SELECT * FROM cache_entries WHERE key = :key LIMIT 1")
    fun observeCache(key: String): Flow<CacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: CacheEntity)

    @Query("DELETE FROM cache_entries WHERE key = :key")
    suspend fun deleteCache(key: String)

    @Query("DELETE FROM cache_entries")
    suspend fun clearAllCache()
}

@Database(entities = [CacheEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fastgit_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
