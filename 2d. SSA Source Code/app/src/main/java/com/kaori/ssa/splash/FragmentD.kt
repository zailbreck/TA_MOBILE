package com.kaori.ssa.splash

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.kaori.ssa.R
import kotlinx.android.synthetic.main.fragment_d.view.*

class FragmentD : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_d, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.finish.setOnClickListener{
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginActivity)
            onConfigureFinished()
        }

        view.back.setOnClickListener{
            viewPager?.currentItem = 2
        }

        return view
    }
        
    private fun onConfigureFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onConfigure", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
        activity?.finish()
    }
}
