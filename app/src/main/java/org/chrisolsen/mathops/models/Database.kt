package org.chrisolsen.mathops.models

import android.content.Context
import androidx.room.*

@Dao
interface GameDao {
    @Insert
    suspend fun insert(game: Game): Long

    @Query("select * from games")
    suspend fun getAll(): List<Game>

    @Query("select * from games where uuid = :gameId")
    suspend fun get(gameId: Int): List<QuizWithQuestions>

    @Query("select * from games where operation = :operation and completed = 1 order by timestamp desc")
    suspend fun getByOperation(operation: String): List<Game>

    @Update
    suspend fun update(game: Game)
}

@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(question: Question): Long

    @Insert
    suspend fun insertAll(vararg questions: Question): List<Long>

    @Update
    suspend fun update(question: Question)
}

data class QuizWithQuestions(
    @Embedded
    val quiz: Game,

    @Relation(parentColumn = "uuid", entityColumn = "quiz_id")
    val questions: List<Question>
)

@Database(entities = [Game::class, Question::class], version = 1)
abstract class MathOpsDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun questionDao(): QuestionDao

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
