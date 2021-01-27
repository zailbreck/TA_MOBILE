package com.kaori.ssa

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler(Looper.getMainLooper()).postDelayed({
            if(onConfigureFinished()){
                findNavController().navigate(R.id.action_splashFragment_to_loginActivity)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 3000)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onConfigureFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onConfigure", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}