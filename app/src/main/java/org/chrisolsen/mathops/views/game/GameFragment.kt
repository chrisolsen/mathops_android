package org.chrisolsen.mathops.views.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        val args = GameFragmentArgs.fromBundle(requireArguments())

        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        Log.d("GameFragment", "onCreateView: operation: ${args.operation}")
        val question = viewModel.generateQuestion(args.operation)
        val answers = viewModel.generateAnswerOptions(question)
        val adapter = context?.let { ArrayAdapter(it, R.layout.question_option, answers) }
        binding.answerOptions.adapter = adapter
        binding.questionNumber1.text = question.value1.toString()
        binding.questionNumber2.text = question.value2.toString()
        binding.questionOperation.text = question.symbol

        binding.answerOptions.setOnItemClickListener { parent: ViewGroup, view: View, position: Int, id: Long ->
            Log.d("GameFragment", "onCreateView: clicked")
        }

        return binding.root
    }
}