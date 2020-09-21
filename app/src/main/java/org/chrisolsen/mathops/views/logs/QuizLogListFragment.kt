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
import kotlinx.android.synthetic.main.fragment_quiz_log_list.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.FragmentQuizLogItemBinding
import org.chrisolsen.mathops.models.Quiz

class QuizLogListFragment private constructor() : Fragment() {

    private val TAG = "QuizLogListFragment"
    private lateinit var vm: QuizLogViewModel
    private lateinit var operation: String
    lateinit var title: String
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_log_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(QuizLogViewModel::class.java)
        vm.fetchByOperation(operation).observe(viewLifecycleOwner, Observer { data ->
            when (data.size) {
                0 -> {
                    empty.visibility = View.VISIBLE
                    quizLogList.visibility = View.GONE
                }
                else -> {
                    empty.visibility = View.GONE
                    quizLogList.visibility = View.VISIBLE
                }
            }
            quizLogList.adapter = QuizLogListAdapter(data)
            quizLogList.layoutManager = LinearLayoutManager(context)
            quizLogList.setHasFixedSize(true)
            quizLogList.addItemDecoration(
                DividerItemDecoration(
                    quizLogList.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, op: String) =
            QuizLogListFragment().apply {
                this.title = title
                this.operation = op
            }
    }

    class QuizLogListAdapter(private val logs: List<Quiz>) :
        RecyclerView.Adapter<QuizLogListAdapter.QuizLogListViewHolder>() {

        class QuizLogListViewHolder(val view: FragmentQuizLogItemBinding) :
            RecyclerView.ViewHolder(view.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizLogListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = DataBindingUtil.inflate<FragmentQuizLogItemBinding>(
                inflater,
                R.layout.fragment_quiz_log_item,
                parent,
                false
            )
            return QuizLogListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return logs.size
        }

        override fun onBindViewHolder(holder: QuizLogListViewHolder, position: Int) {
            holder.view.quiz = logs.get(position)
        }
    }
}