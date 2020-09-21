package org.chrisolsen.mathops.views.quiz

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import org.chrisolsen.mathops.models.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt
import kotlin.random.Random

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val TAG = "QuizViewModel"
    }

    private lateinit var quiz: Quiz
    private lateinit var questions: MutableList<Question>

    private lateinit var quizDao: QuizDao
    private lateinit var questionDao: QuestionDao

    private var questionStartTime: Long = -1
    private var questionIndex = 0

    val percentCorrect: Int
        get() {
            return (quiz.correctCount.toFloat() * 100 / quiz.questionCount).roundToInt()
        }

    val percentComplete: Int
        get() {
            return if (isLastQuestion()) 100 else (100f * questionIndex / questions.size).toInt()
        }

    fun isLastQuestion(): Boolean {
        return questionIndex >= questions.size
    }

    fun getNextQuestion(): Question {
        return questions[questionIndex++]
    }

    suspend fun startQuiz(questionCount: Int, operation: String) {
        questionIndex = 0
        questionStartTime = Date().time

        quizDao = MathOpsDatabase(getApplication()).quizDao()
        questionDao = MathOpsDatabase(getApplication()).questionDao()

        quiz = Quiz(questionCount = questionCount, operation = operation, timestamp = Date().time)
        quiz.uuid = quizDao.insert(quiz)

        // weight map
        val weightMap = HashMap<String, Int>()
        quizDao.getWeights(operation).forEach { qw ->
            weightMap[qw.equation] = qw.weight
        }

        // get weights from previous quizzes...for now everything is 1
        val quizWeights = HashMap<Question, Int>()
        for (i in 2..9) {
            for (j in 2..9) {
                val question = when (operation) {
                    "+", "x" -> Question(
                        quizId = quiz.uuid,
                        value1 = i,
                        value2 = j,
                        operation = operation
                    )
                    "/" -> Question(
                        quizId = quiz.uuid,
                        value1 = i * j,
                        value2 = i,
                        operation = operation
                    )
                    "-" -> Question(
                        quizId = quiz.uuid,
                        value1 = i + j,
                        value2 = i,
                        operation = operation
                    )
                    else -> throw Exception("Invalid operation")
                }
                question.calculate()

                val weight = weightMap["${question.value1}${operation}${question.value2}"]
                quizWeights.set(question, weight ?: 1)
            }
        }

        // generate question weighted list for this quiz
        val questionPool = mutableListOf<Question>()
        quizWeights.keys.forEach { question ->
            quizWeights.get(question)?.let { weight ->
                for (i in 0..weight) {
                    questionPool.add(question.copy())
                }
            }
        }
        Log.d(TAG, "startQuiz: quizWeights => ${quizWeights.size}")

        // pluck out questions from weighted list
        questions = mutableListOf<Question>()
        for (i in 1..quiz.questionCount) {
            val index = Random.nextInt(questionPool.size - 1)
            questionPool[index].let { question ->
                questions.add(question)
                questionPool.removeAt(index)
            }
        }
        val ids = questionDao.insertAll(*questions.toTypedArray())
        ids.forEachIndexed { index, id ->
            questions[index].uuid = id
        }
    }

    // Update database question with the answered value
    suspend fun answerQuestion(question: Question, answer: Int): Boolean {
        val isCorrect = question.answer == answer
        question.correct = isCorrect
        question.response = answer
        question.time = (Date().time - questionStartTime).toInt()
        questionDao.update(question)

        // reset time
        questionStartTime = Date().time

        return isCorrect
    }

    // Generates a list of possible answers for the question
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

    suspend fun finishQuiz() {
        quiz.duration = Date().time - quiz.timestamp
        quiz.correctCount =
            questions.map { q -> if (q.correct) 1 else 0 }.reduce { acc, num -> acc + num }
        quiz.completed = true
        quizDao.update(quiz)
    }
}