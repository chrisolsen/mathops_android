package org.chrisolsen.mathops.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "games")
data class Game(
    @ColumnInfo(name = "question_count")
    val questionCount: Int,
    @ColumnInfo(name = "correct_count")
    val correctCount: Int,

    val operation: String,
    val timestamp: Long,
    val duration: Long
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0

    val happenedOn: String
        get() {
            return dateFormatter.format(Date(this.timestamp))
        }

    val timePerQuestion: String
        get() {
            return String.format("%.1f", duration / 1000f / questionCount)
        }

    val score: String
        get() {
            return String.format("%.0f", 100f * correctCount / questionCount)
        }

    companion object {
        val dateFormatter = SimpleDateFormat("MMM d, yyyy")
    }
}
