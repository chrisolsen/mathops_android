package org.chrisolsen.mathops.views.gameoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameOptionsFragmentBinding
import org.chrisolsen.mathops.views.game.Operations

class GameOptionsFragment : Fragment() {

    companion object {
        fun newInstance() = GameOptionsFragment()
    }

    val TAG = "GameOptionsFragment"

    private lateinit var viewModel: GameOptionsViewModel
    private lateinit var binding: GameOptionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.game_options_fragment, container, false)

        binding.startGame.setOnClickListener { view: View -> startGame(view) }

        binding.addition.setOnClickListener { _ ->
            viewModel.operation.value = Operations.addition
        }
        binding.subtraction.setOnClickListener { _ ->
            viewModel.operation.value = Operations.subtraction
        }
        binding.multiplication.setOnClickListener { _ ->
            viewModel.operation.value = Operations.multiplication
        }
        binding.division.setOnClickListener { _ ->
            viewModel.operation.value = Operations.division
        }

        binding.questionCountSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = progress.toFloat() / 100 * 50
                viewModel.questionCount.value = p.toInt()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return binding.root
    }

    fun startGame(view: View) {
        view
            .findNavController()
            .navigate(
                GameOptionsFragmentDirections.actionGameOptionsFragmentToGameFragment(
                    10,
                    Operations.addition
                )
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider.NewInstanceFactory().create(GameOptionsViewModel::class.java)

        viewModel.questionCount.observe(viewLifecycleOwner, Observer { count ->
            val c = if (count == 0) 1 else count
            binding.questionCountValue.text = c.toString()
        })
    }
}

