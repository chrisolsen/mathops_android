package org.chrisolsen.mathops.views.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.chrisolsen.mathops.R
import org.chrisolsen.mathops.databinding.FragmentLandingBinding

class LandingFragment : Fragment() {

    private val TAG = "LandingFragment"
    private lateinit var binding: FragmentLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }
}