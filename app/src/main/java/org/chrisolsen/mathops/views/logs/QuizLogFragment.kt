package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_quiz_log.*
import org.chrisolsen.mathops.R

class QuizLogFragment : Fragment() {

    companion object {
        private const val TAG = "QuizLogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let {
            val pagerAdapter = ViewPagerAdapter(it)
            viewPager.adapter = pagerAdapter
            viewPager.currentItem = 0

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = pagerAdapter.fragments[position].title
            }.attach()
        }
    }

    class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        var fragments: List<QuizLogListFragment> = listOf(
            QuizLogListFragment.newInstance("Add", "+"),
            QuizLogListFragment.newInstance("Subtract", "-"),
            QuizLogListFragment.newInstance("Multiply", "x"),
            QuizLogListFragment.newInstance("Divide", "/")
        )

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}