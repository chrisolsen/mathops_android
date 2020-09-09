package org.chrisolsen.mathops.views.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.game_log_fragment.*
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.views.game.Operation

class GameLogFragment : Fragment() {

    companion object {
        private const val TAG = "GameLogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_log_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout.setupWithViewPager(viewPager)
        Log.d(TAG, "onViewCreated: ")
        activity?.let {
            Log.d(TAG, "onViewCreated: creating fragments")
            val pagerAdapter = ViewPagerAdapter(it.supportFragmentManager, 0)
            pagerAdapter.fragments = listOf(
                GameLogListFragment.newInstance(Operation.addition),
                GameLogListFragment.newInstance(Operation.subtraction),
                GameLogListFragment.newInstance(Operation.multiplication),
                GameLogListFragment.newInstance(Operation.division)
            )
            viewPager.adapter = pagerAdapter
            viewPager.currentItem = 0
        }
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        val TAG = "ViewPagerAdapter"
        lateinit var fragments: List<GameLogListFragment>

        override fun getPageTitle(position: Int): CharSequence? {
            return fragments[position].title
        }

        override fun getItem(position: Int): Fragment {
            Log.d(TAG, "getItem: $position")
            return fragments[position]
        }

        override fun getCount(): Int {
            return 4
        }
    }
}