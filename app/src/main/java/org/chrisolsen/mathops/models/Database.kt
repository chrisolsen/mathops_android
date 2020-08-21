package org.chrisolsen.mathops.models

import android.content.Context
import androidx.room.*

@Dao
interface GameDao {
    @Insert
    suspend fun insert(game: Game): Long

    @Query("Select * from games")
    suspend fun getAll(): List<Game>
}

@Database(entities = [Game::class], version = 1)
abstract class MathOpsDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var instance: MathOpsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, MathOpsDatabase::class.java, "mathops"
        ).build()
    }
}
