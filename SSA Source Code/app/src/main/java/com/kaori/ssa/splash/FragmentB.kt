package com.kaori.ssa.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.kaori.ssa.R
import kotlinx.android.synthetic.main.fragment_b.view.*

class FragmentB : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_b, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.back.setOnClickListener{
            viewPager?.currentItem = 0
        }

        view.next.setOnClickListener{
            viewPager?.currentItem = 2
        }

        return view
    }
}