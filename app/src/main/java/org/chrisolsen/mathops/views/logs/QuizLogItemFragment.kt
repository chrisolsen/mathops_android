package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.models.Quiz

class QuizLogItemFragment private constructor(private val quiz: Quiz) : Fragment() {

    private val TAG = "QuizLogItemFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: inflating quizlogitemfragment")
        return inflater.inflate(R.layout.fragment_quiz_log_item, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(quiz: Quiz) = QuizLogItemFragment(quiz)
    }
}