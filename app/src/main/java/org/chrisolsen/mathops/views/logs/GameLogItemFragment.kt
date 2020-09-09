package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.models.Game

class GameLogItemFragment private constructor(private val game: Game) : Fragment() {

    private val TAG = "GameLogItemFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: inflating gamelogitemfragment")
        return inflater.inflate(R.layout.fragment_game_log_item, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(game: Game) = GameLogItemFragment(game)
    }
}