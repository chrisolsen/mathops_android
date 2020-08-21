package org.chrisolsen.mathops.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
}
