package com.kaorimaps.ssa.menu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.kaorimaps.ssa.LoginActivity
import com.kaorimaps.ssa.R
import kotlinx.android.synthetic.main.fragment_accounts.*
import kotlinx.android.synthetic.main.fragment_accounts.view.*

class AccountFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    private fun setupFirebase(){
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_accounts, container, false)
        setupFirebase()
        updateUI(view)
        return view
    }

    private fun updateUI(view: View){
        activity?.title = arguments?.getString("fragmentName")
        view.setBackgroundColor(Color.WHITE)
        view.btnLogout.setOnClickListener{
            logout()
        }
    }

    private fun logout(){
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    override fun onStart() {
        super.onStart()
        getUserInfo()
    }

    private fun getUserInfo(){
        val user = FirebaseAuth.getInstance().currentUser
        var pUrl = ""
        var state = false
        for (profile in user!!.providerData) {
            // check if the provider id matches "facebook.com"
            if (FacebookAuthProvider.PROVIDER_ID == profile.providerId) {
                pUrl = profile.uid
                state = true
            }
        }

        val photoUrl = "https://graph.facebook.com/$pUrl/picture?height=500"

        user.let {
            txtUser.text = user.displayName
            uid.text = user.uid
            if(state){
                Glide.with(requireContext()).load(photoUrl).into(photo)
            }else{
                Glide.with(requireContext()).load(user.photoUrl).into(photo)
            }
        }
    }
}