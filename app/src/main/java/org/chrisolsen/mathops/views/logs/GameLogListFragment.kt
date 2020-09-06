package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_game_log_list.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.FragmentGameLogItemBinding
import org.chrisolsen.mathops.models.Game
import org.chrisolsen.mathops.views.game.Operation

class GameLogListFragment : Fragment() {

    private val TAG = "GameLogListFragment"
    private lateinit var operation: Operation
    private lateinit var vm: GameLogViewModel

    val title: String
        get() = operation.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_log_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(GameLogViewModel::class.java)
        Log.d(TAG, "onViewCreated: fetching by operation: $operation")
        vm.fetchByOperation(operation).observe(viewLifecycleOwner, Observer { data ->
            Log.d(TAG, "onViewCreated: observer list: ${data.size}")
            gameLogList.adapter = GameLogListAdapter(data)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(op: Operation) =
            GameLogListFragment().apply {
                operation = op
            }
    }

    class GameLogListAdapter(private val logs: List<Game>) :
        RecyclerView.Adapter<GameLogListAdapter.GameLogListViewHolder>() {

        private val TAG = "GameLogListAdapter"

        class GameLogListViewHolder(val view: FragmentGameLogItemBinding) :
            RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameLogListViewHolder {
            Log.d(TAG, "onCreateViewHolder")
            val inflater = LayoutInflater.from(parent.context)
            val view = DataBindingUtil.inflate<FragmentGameLogItemBinding>(
                inflater,
                R.layout.fragment_game_log_item,
                parent,
                false
            )
            return GameLogListViewHolder(view)
        }

        override fun getItemCount(): Int {
            Log.d(TAG, "getItemCount: logs: ${logs.size}")
            return logs.size
        }

        override fun onBindViewHolder(holder: GameLogListViewHolder, position: Int) {
            Log.d(TAG, "onBindViewHolder: binding game for position: $position")
            holder.view.game = logs.get(position)
        }
    }
}