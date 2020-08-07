package org.chrisolsen.mathops.views.game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private var answerAnimation: AnimatorSet? = null
    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameFragmentBinding
    private var questionsAsked = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        val args = GameFragmentArgs.fromBundle(requireArguments())
        viewModel.init(args.questionCount, args.operation)
        askQuestion(args)

        return binding.root
    }

    private fun askQuestion(args: GameFragmentArgs) {
        questionsAsked++
        if (questionsAsked >= viewModel.questionCount) {
            // TODO: show the game score here
            Log.d("GameFragment", "askQuestion: GAME OVER")
            return
        }

        val question = viewModel.generateQuestion()
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
            askQuestion(args)
        }
    }

    fun animateAnswerStatus(view: View) {
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

        binding.progressBar.progress =
            binding.progressBar.progress + (100 / viewModel.questionCount)
    }
}