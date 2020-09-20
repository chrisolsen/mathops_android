package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_game_log_list.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.FragmentGameLogItemBinding
import org.chrisolsen.mathops.models.Game

class GameLogListFragment private constructor() : Fragment() {

    private val TAG = "GameLogListFragment"
    private lateinit var vm: GameLogViewModel
    private lateinit var operation: String
    lateinit var title: String
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_log_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(GameLogViewModel::class.java)
        vm.fetchByOperation(operation).observe(viewLifecycleOwner, Observer { data ->
            when (data.size) {
                0 -> {
                    empty.visibility = View.VISIBLE
                    gameLogList.visibility = View.GONE
                }
                else -> {
                    empty.visibility = View.GONE
                    gameLogList.visibility = View.VISIBLE
                }
            }
            gameLogList.adapter = GameLogListAdapter(data)
            gameLogList.layoutManager = LinearLayoutManager(context)
            gameLogList.setHasFixedSize(true)
            gameLogList.addItemDecoration(
                DividerItemDecoration(
                    gameLogList.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, op: String) =
            GameLogListFragment().apply {
                this.title = title
                this.operation = op
            }
    }

    class GameLogListAdapter(private val logs: List<Game>) :
        RecyclerView.Adapter<GameLogListAdapter.GameLogListViewHolder>() {

        class GameLogListViewHolder(val view: FragmentGameLogItemBinding) :
            RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameLogListViewHolder {
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
            return logs.size
        }

        override fun onBindViewHolder(holder: GameLogListViewHolder, position: Int) {
            holder.view.game = logs.get(position)
        }
    }
}