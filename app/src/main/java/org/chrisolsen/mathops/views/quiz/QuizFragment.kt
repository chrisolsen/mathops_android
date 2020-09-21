package org.chrisolsen.mathops.views.quiz

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.coroutines.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.FragmentQuizBinding
import org.chrisolsen.mathops.models.Question

class QuizFragment : Fragment() {
    private val TAG = "QuizFragment"

    private lateinit var viewModel: QuizViewModel
    private lateinit var binding: FragmentQuizBinding
    private var answerAnimation: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false)
        viewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = QuizFragmentArgs.fromBundle(requireArguments())

        // initialization
        val banner = binding.banner
        banner.quizscoreBackground.alpha = 0f
        banner.quizscoreForeground.y = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        banner.quizscorePlayAgain.setOnClickListener { view: View ->
            banner.quizscoreBackground.animate().alpha(0f)
            banner.quizscoreForeground.animate().run {
                y(Resources.getSystem().displayMetrics.heightPixels.toFloat())
                interpolator = AnticipateOvershootInterpolator()
                duration = 500
            }
            ObjectAnimator.ofInt(binding.progressBar, "progress", 0).start()
            startQuiz(args.questionCount, args.operation)
        }

        startQuiz(args.questionCount, args.operation)
    }

    private fun startQuiz(questionCount: Int, operation: String) {
        GlobalScope.launch {
            viewModel.startQuiz(questionCount, operation)
            withContext(Dispatchers.Main) {
                askQuestion(questionCount, operation)
            }
        }
    }

    private suspend fun askQuestion(questionCount: Int, operation: String) {
        val question: Question = viewModel.getNextQuestion()
        binding.questionNumber1.text = question.value1.toString()
        binding.questionNumber2.text = question.value2.toString()
        binding.questionOperation.text = question.operation

        val answers = viewModel.generateAnswerOptions(question)
        val adapter = context?.let { ArrayAdapter(it, R.layout.question_option, answers) }
        binding.answerOptions.adapter = adapter
        binding.answerOptions.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            viewModel.viewModelScope.launch {
                if (viewModel.answerQuestion(question, answers[position])) {
                    animateAnswerStatus(correct)
                } else {
                    animateAnswerStatus(incorrect)
                }

                delay(1000)
                if (viewModel.isLastQuestion()) {
                    withContext(Dispatchers.IO) {
                        viewModel.finishQuiz()
                    }

                    val banner = binding.banner
                    banner.quizscoreScore.text = viewModel.percentCorrect.toString() + "%"
                    banner.quizscoreText.text = when (viewModel.percentCorrect) {
                        in 0..50 -> "Keep Trying!"
                        in 51..60 -> "Getting Better!"
                        in 61..79 -> "Keep It Up!"
                        in 80..89 -> "Good Job!"
                        else -> "Great Work!!"
                    }
                    banner.quizscoreBackground.animate().alpha(1f)
                    banner.quizscoreForeground.animate().run {
                        y(200f)
                        interpolator = AnticipateOvershootInterpolator()
                        duration = 1000
                    }
                } else {
                    askQuestion(questionCount, operation)
                }
            }
        }
    }

    private fun animateAnswerStatus(view: View) {
        val fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f).apply { duration = 600 }
        val fadeOut = ObjectAnimator.ofFloat(view, View.ALPHA, 0f).apply { duration = 400 }
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 5.0f).apply { duration = 1000 }
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 5.0f).apply { duration = 1000 }
        val resetScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f).apply { duration = 0 }
        val resetScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f).apply { duration = 0 }

        // end it if it's already running
        answerAnimation?.run {
            end()
        }
        answerAnimation = AnimatorSet().apply {
            play(fadeIn).with(scaleX).with(scaleY)
            play(fadeOut).after(fadeIn)
            play(resetScaleX).with(resetScaleY).after(fadeOut)
            start()
        }

        // progress bar animation
        ObjectAnimator.ofInt(binding.progressBar, "progress", viewModel.percentComplete).start()
    }
}