package org.chrisolsen.mathops.views.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.chrisolsen.mathops.R

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var binding: ViewDataBinding
    private lateinit var operation: Operations
    private var questionCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        val args = GameFragmentArgs.fromBundle(requireArguments())
        questionCount = args.questionCount
        operation = args.operation

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
    }
}