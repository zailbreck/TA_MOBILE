package com.kaori.ssa.config.cPager

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaori.ssa.R
import com.kaori.ssa.splash.FragmentA
import com.kaori.ssa.splash.FragmentB
import com.kaori.ssa.splash.FragmentC
import com.kaori.ssa.splash.FragmentD
import kotlinx.android.synthetic.main.fragment_view_pager.view.*


class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FragmentA(),
            FragmentB(),
            FragmentC(),
            FragmentD()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.setBackgroundColor(Color.WHITE)
        view.viewPager.adapter = adapter


        return view
    }



}