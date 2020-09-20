package org.chrisolsen.mathops.views.gameoptions

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.GameOptionsFragmentBinding

class GameOptionsFragment : Fragment() {

    private var questionCount = 25
    private var operation = "+"
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

        binding.startGame.setOnClickListener { v: View -> startGame(v) }
        binding.addition.setOnClickListener { operation = "+" }
        binding.subtraction.setOnClickListener { operation = "-" }
        binding.multiplication.setOnClickListener { operation = "x" }
        binding.division.setOnClickListener { operation = "/" }

        binding.questionCountSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = (progress.toFloat() / 100 * 50).toInt()
                questionCount = if (p == 0) 1 else p
                binding.questionCountValue.text = questionCount.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startGame(view: View) {
        view
            .findNavController()
            .navigate(
                GameOptionsFragmentDirections
                    .actionGameOptionsFragmentToGameFragment(questionCount, operation)
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

