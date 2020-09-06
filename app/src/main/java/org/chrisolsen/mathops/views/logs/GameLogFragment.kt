package org.chrisolsen.mathops.views.logs

import android.os.Bundle
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
    private val TAG = "GameLogFragment"

    private lateinit var additionFragment: GameLogListFragment
    private lateinit var subtractionFragment: GameLogListFragment
    private lateinit var multiplicationFragment: GameLogListFragment
    private lateinit var divisionFragment: GameLogListFragment

    private lateinit var vm: GameLogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_log_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        additionFragment = GameLogListFragment.newInstance(Operation.addition)
        subtractionFragment = GameLogListFragment.newInstance(Operation.subtraction)
        multiplicationFragment = GameLogListFragment.newInstance(Operation.multiplication)
        divisionFragment = GameLogListFragment.newInstance(Operation.division)

        tabLayout.setupWithViewPager(viewPager)

        activity?.let {
            val pagerAdapter = ViewPagerAdapter(it.supportFragmentManager, 0)
            pagerAdapter.fragments = listOf(
                additionFragment,
                subtractionFragment,
                multiplicationFragment,
                divisionFragment
            )
            viewPager.adapter = pagerAdapter
        }
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        lateinit var fragments: List<GameLogListFragment>

        override fun getPageTitle(position: Int): CharSequence? {
            return fragments.get(position).title
        }

        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }

        override fun getCount(): Int {
            return 4
        }
    }
}