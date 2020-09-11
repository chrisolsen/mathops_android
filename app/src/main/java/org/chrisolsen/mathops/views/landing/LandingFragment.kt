package org.chrisolsen.mathops.views.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.LandingFragmentBinding

class LandingFragment : Fragment() {

    private val TAG = "LandingFragment"
    private lateinit var binding: LandingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.landing_fragment, container, false)

        binding.getStarted.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(
                    LandingFragmentDirections.actionLandingFragmentToGameOptionsFragment(),
                )
        }
        activity?.title = ""
        return binding.root
    }
}