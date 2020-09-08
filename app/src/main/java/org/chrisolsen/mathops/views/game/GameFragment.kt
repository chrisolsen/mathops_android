package org.chrisolsen.mathops.views.game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameFragmentBinding

class GameFragment : Fragment() {
    private val TAG = "GameFragment"

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameFragmentBinding
    private var answerAnimation: AnimatorSet? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var gameScoreBannerForeground: View
    private lateinit var gameScoreBackground: View
    private lateinit var gameScorePlayAgainButton: Button
    private lateinit var gameScoreScore: TextView
    private lateinit var gameScoreText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = GameFragmentArgs.fromBundle(requireArguments())
        viewModel.init(args.questionCount, args.operation)

        // TODO: move this into a custom view
        gameScoreBannerForeground = view.findViewById(R.id.gameScoreForeground)
        gameScoreBackground = view.findViewById(R.id.gameScoreBackground)
        gameScorePlayAgainButton = view.findViewById(R.id.gameScorePlayAgain)
        gameScoreScore = view.findViewById(R.id.gameScoreScore)
        gameScoreText = view.findViewById(R.id.gameScoreText)

        // initialization
        gameScoreBackground.alpha = 0f
        gameScoreBannerForeground.y = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        gameScorePlayAgainButton.setOnClickListener { view: View ->
            gameScoreBackground.animate().alpha(0f)
            gameScoreBannerForeground.animate().run {
                y(Resources.getSystem().displayMetrics.heightPixels.toFloat())
                interpolator = AnticipateOvershootInterpolator()
                duration = 500
            }
            ObjectAnimator.ofInt(binding.progressBar, "progress", 0).start()
            viewModel.reset()
            askQuestion(args)
        }

        askQuestion(args)
    }

    private fun askQuestion(args: GameFragmentArgs) {
        val question: Question = viewModel.currentQuestion
        binding.questionNumber1.text = question.value1.toString()
        binding.questionNumber2.text = question.value2.toString()
        binding.questionOperation.text = question.symbol

        val answers = viewModel.generateAnswerOptions(question)
        val adapter = context?.let { ArrayAdapter(it, R.layout.question_option, answers) }
        binding.answerOptions.adapter = adapter
        binding.answerOptions.setOnItemClickListener { _: ViewGroup, _: View, position: Int, _: Long ->
            if (viewModel.answerQuestion(question, answers.get(position))) {
                animateAnswerStatus(binding.correct)
            } else {
                animateAnswerStatus(binding.incorrect)
            }

            scope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    if (viewModel.currentQuestionNumber >= viewModel.questionCount) {
//                        binding.gameScoreBackground.isClickable = true
                        gameScoreScore.text = viewModel.percentCorrect.toString() + "%"
                        gameScoreText.text = when (viewModel.percentCorrect) {
                            in 0..50 -> "Keep Trying!"
                            in 51..60 -> "Getting Better!"
                            in 61..79 -> "Keep It Up!"
                            in 80..89 -> "Good Job!"
                            else -> "Great Work!!"
                        }
                        withContext(Dispatchers.IO) {
                            viewModel.saveGame()
                        }
                        gameScoreBackground.animate().alpha(1f)
                        gameScoreBannerForeground.animate().run {
                            y(200f)
                            interpolator = AnticipateOvershootInterpolator()
                            duration = 1000
                        }
                    } else {
                        askQuestion(args)
                    }
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
        answerAnimation?.run {
            end()
        }
        answerAnimation = AnimatorSet().apply {
            play(fadeIn).with(scaleX).with(scaleY)
            play(fadeOut).after(fadeIn)
            play(resetScaleX).with(resetScaleY).after(fadeOut)
            start()
        }

        var progress = binding.progressBar.progress + (100 / viewModel.questionCount)
        val isLastQuestion = viewModel.questionCount == viewModel.currentQuestionNumber
        progress = if (isLastQuestion) 100 else progress
        ObjectAnimator.ofInt(binding.progressBar, "progress", progress).start()
    }
}