package org.chrisolsen.mathops.views.gameoptions

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameOptionsFragmentBinding
import org.chrisolsen.mathops.views.game.Operation

class GameOptionsFragment : Fragment() {

    private lateinit var viewModel: GameOptionsViewModel
    private lateinit var binding: GameOptionsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.game_options_fragment, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startGame.setOnClickListener { view: View -> startGame(view) }

        binding.addition.setOnClickListener { _ ->
            viewModel.operation.value = Operation.addition
        }
        binding.subtraction.setOnClickListener { _ ->
            viewModel.operation.value = Operation.subtraction
        }
        binding.multiplication.setOnClickListener { _ ->
            viewModel.operation.value = Operation.multiplication
        }
        binding.division.setOnClickListener { _ ->
            viewModel.operation.value = Operation.division
        }

        binding.questionCountSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = (progress.toFloat() / 100 * 50).toInt()
                viewModel.questionCount.value = if (p == 0) 1 else p
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel = ViewModelProvider(this).get(GameOptionsViewModel::class.java)
        viewModel.questionCount.observe(viewLifecycleOwner, Observer { count ->
            val c = if (count == 0) 1 else count
            binding.questionCountValue.text = c.toString()
        })
    }

    private fun startGame(view: View) {
        view
            .findNavController()
            .navigate(
                GameOptionsFragmentDirections.actionGameOptionsFragmentToGameFragment(
                    viewModel.questionCount.value!!,
                    viewModel.operation.value!!
                )
            )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_game_stats -> {
                view?.findNavController()?.navigate(R.id.gameLogsFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

