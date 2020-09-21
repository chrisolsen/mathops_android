package org.chrisolsen.mathops.models

import android.content.Context
import androidx.room.*

@Dao
interface QuizDao {
    @Insert
    suspend fun insert(quiz: Quiz): Long

    @Query("select * from quizzes")
    suspend fun getAll(): List<Quiz>

    @Query("select * from quizzes where uuid = :quizId")
    suspend fun get(quizId: Int): List<QuizWithQuestions>

    @Query("select * from quizzes where operation = :operation and completed = 1 order by timestamp desc")
    suspend fun getByOperation(operation: String): List<Quiz>

    @Update
    suspend fun update(quiz: Quiz)

    @Query(
        """select 
        value1 || operation || value2 as equation, 
        1+cast(round(9 * (1.0 - (1.0 * sum(correct) / count(*))), 0) as int) as weight 
        from questions 
        where operation = :operation and time > 0 
        group by equation"""
    )
    suspend fun getWeights(operation: String): List<QuestionWeight>
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
    val quiz: Quiz,

    @Relation(parentColumn = "uuid", entityColumn = "quiz_id")
    val questions: List<Question>
)

@Database(entities = [Quiz::class, Question::class], version = 1)
abstract class MathOpsDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
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
