package org.chrisolsen.mathops.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

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

@Entity(tableName = "games")
data class Game(
    @ColumnInfo(name = "question_count")
    val questionCount: Int,
    @ColumnInfo(name = "correct_count")
    var correctCount: Int = 0,
    val operation: String,
    val timestamp: Long = 0L,
    var duration: Long = 0L,
    var completed: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Long = 0
    val happenedOn: String
        get() = dateFormatter.format(Date(this.timestamp))
    val timePerQuestion: String
        get() = String.format("%.1f", duration / 1000f / questionCount)
    val score: String
        get() = String.format("%.0f", 100f * correctCount / questionCount)

    companion object {
        val dateFormatter = SimpleDateFormat("MMM d, yyyy")
    }
}
