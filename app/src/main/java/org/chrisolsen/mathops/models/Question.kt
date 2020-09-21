package org.chrisolsen.mathops.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @ColumnInfo(name = "quiz_id")
    val quizId: Long,
    val value1: Int,
    val value2: Int,
    val operation: String,
    var answer: Int = 0,
    var correct: Boolean = false,
    var response: Int = 0,
    var time: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Long = 0

    fun calculate() {
        answer = when (operation) {
            "+" -> value1 + value2
            "/" -> value1 / value2
            "x" -> value1 * value2
            "-" -> value1 - value2
            else -> throw Exception("Invalid operation")
        }
    }
}
