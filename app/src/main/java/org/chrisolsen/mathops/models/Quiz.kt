package org.chrisolsen.mathops.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "quizzes")
data class Quiz(
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
    val percent: String
        get() = String.format("%.0f", 100f * correctCount / questionCount)
    val score: String
        get() = "$correctCount / $questionCount"

    companion object {
        val dateFormatter = SimpleDateFormat("MMM d, yyyy")
    }
}
