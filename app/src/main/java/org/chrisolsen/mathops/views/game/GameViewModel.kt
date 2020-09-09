package org.chrisolsen.mathops.views.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.chrisolsen.mathops.models.Game
import org.chrisolsen.mathops.models.MathOpsDatabase
import java.util.*
import kotlin.math.roundToInt

enum class Operation {
    addition,
    subtraction,
    multiplication,
    division,
}

class Question(val value1: Int, val value2: Int, val operation: Operation) {

    private var usersResponse: Int? = null

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
        usersResponse = value
        return value == answer
    }

    fun isAnswered(): Boolean {
        return usersResponse != null
    }
}

class IncorrectQuestion(val question: Question, val questionIndex: Int)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "GameViewModel"

    private var startTime = 0L
    private var questionIndex = -1
    private val showAgainOffset = 5
    private val incorrectQuestions = mutableListOf<IncorrectQuestion>()
    private val previousQuestions = mutableListOf<Question>()

    lateinit var operation: Operation
        private set

    var questionCount = 0
        private set

    val correctCount: Int
        get() {
            return questionCount - incorrectQuestions.size
        }

    val durationMilliSeconds: Long
        get() {
            return Date().time - startTime
        }

    fun init(questionCount: Int, operation: Operation) {
        this.questionCount = questionCount
        this.operation = operation
        this.startTime = Date().time
    }

    val currentQuestionNumber: Int
        get() {
            return questionIndex + 1
        }

    private var _currentQuestion: Question? = null
    var currentQuestion: Question
        get() {
            if (_currentQuestion == null || _currentQuestion!!.isAnswered()) {
                return generateQuestion()
            }
            return _currentQuestion!!
        }
        private set(question) {
            _currentQuestion = question
        }

    val percentCorrect: Int
        get() {
            return ((questionCount - incorrectQuestions.size).toFloat() * 100 / questionCount).roundToInt()
        }

    fun reset() {
        questionIndex = -1
        incorrectQuestions.clear()
        previousQuestions.clear()
        startTime = Date().time
    }

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
                    val value = if (question.answer > 0) Math.abs(r) else -Math.abs(r)
                    if (!list.contains(value)) {
                        list[i] = value
                        break
                    }
                }
            }
        }

        return list.asList()
    }

    suspend fun saveGame() {
        val game = Game(
            operation = operation.toString(),
            duration = durationMilliSeconds,
            correctCount = correctCount,
            questionCount = questionCount,
            timestamp = Date().time
        )
        MathOpsDatabase(getApplication()).gameDao().insert(game)
    }

    private fun generateQuestion(): Question {
        questionIndex++
        // re-ask previously incorrect question
        if (incorrectQuestions.isNotEmpty()) {
            val useIncorrectQuestion =
                incorrectQuestions.first().questionIndex + showAgainOffset <= questionIndex
            if (useIncorrectQuestion) {
                val incorrectQuestion = incorrectQuestions.removeAt(0)
                return incorrectQuestion.question
            }
        }

        // generate new question
        var attemptCount = 0
        existing@ while (true) {
            val num1 = (2..9).random()
            val num2 = (2..9).random()
            val newQuestion = when (operation) {
                Operation.multiplication, Operation.addition -> {
                    Question(num1, num2, operation)
                }
                Operation.subtraction -> {
                    Question(num1 + num2, num2, operation)
                }
                Operation.division -> {
                    Question(num1 * num2, num2, operation)
                }
            }

            attemptCount++
            for (q in previousQuestions) {
                if (q != newQuestion) {
                    currentQuestion = newQuestion
                    break@existing
                }
            }

            if (attemptCount > 99) {
                currentQuestion = newQuestion
                break@existing
            }
        }

        previousQuestions.add(currentQuestion!!)

        return currentQuestion!!
    }

    fun answerQuestion(question: Question, answer: Int): Boolean {
        if (!question.isCorrect(answer)) {
            incorrectQuestions.add(IncorrectQuestion(question, questionIndex))
            return false
        }
        return true
    }
}