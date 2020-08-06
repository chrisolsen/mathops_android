package org.chrisolsen.mathops.views.game

import android.util.Log
import androidx.lifecycle.ViewModel

enum class Operation {
    addition,
    subtraction,
    multiplication,
    division,
}

data class Question(val value1: Int, val value2: Int, val operation: Operation) {

    val answer: Int
        get() {
            return when (operation) {
                Operation.addition -> value1 + value2
                Operation.division -> value1 / value2
                Operation.multiplication -> value1 * value2
                Operation.subtraction -> value1 - value2
            }
        }

    val symbol: String
        get() {
            return when (operation) {
                Operation.addition -> "+"
                Operation.division -> "/"
                Operation.multiplication -> "x"
                Operation.subtraction -> "-"
            }
        }

    fun isCorrect(value: Int): Boolean {
        return value == answer
    }
}

data class IncorrectQuestion(val question: Question, val questionIndex: Int)

class GameViewModel : ViewModel() {
    private val TAG = "GameViewModel"

    private val offset = 5
    private var questionIndex = 0
    private val incorrectQuestions = mutableListOf<IncorrectQuestion>()
    private val previousQuestions = mutableListOf<Question>()

    fun generateAnswerOptions(question: Question): List<Int> {
        val list = IntArray(4)
        val minValue = question.answer - 10
        val maxValue = question.answer + 10
        val correctIndex = (0..3).random()
        list[correctIndex] = question.answer

        for (i in 0..3) {
            if (i != correctIndex) {
                while (true) {
                    val r = (minValue..maxValue).random()
                    if (!list.contains(r)) {
                        list[i] = r
                        break
                    }
                }
            }
        }

        return list.asList()
    }

    fun generateQuestion(operation: Operation): Question {
        questionIndex++
        // re-ask previously incorrect question
        if (incorrectQuestions.isNotEmpty()) {
            val useIncorrectQuestion =
                incorrectQuestions.first().questionIndex + offset <= questionIndex
            if (useIncorrectQuestion) {
                val incorrectQuestion = incorrectQuestions.removeAt(0)
                return incorrectQuestion.question
            }
        }

        // generate new question
        var attemptCount = 0
        var question = Question(7, 9, operation)
        existing@ while (true) {
            val num1 = (2..9).random()
            val num2 = (2..9).random()
            val newQuestion = Question(num1, num2, operation)

            attemptCount++
            for (q in previousQuestions) {
                if (q != newQuestion) {
                    question = newQuestion
                    break@existing
                }
            }

            if (attemptCount > 99) {
                question = newQuestion
                break@existing
            }
        }

        previousQuestions.add(question)

        Log.d(TAG, "generateQuestion: $question")
        return question
    }

    fun answerQuestion(question: Question, answer: Int): Boolean {
        if (!question.isCorrect(answer)) {
            incorrectQuestions.add(IncorrectQuestion(question, questionIndex))
            return false
        }
        return true
    }
}