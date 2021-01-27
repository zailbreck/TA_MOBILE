package com.kaori.ssa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity() {
    val RC_SIGN_IN: Int = 1
    lateinit var signInClient: GoogleSignInClient
    lateinit var signInOptions: GoogleSignInOptions
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val TAG = "FacebookLogin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)
        super.onCreate(savedInstanceState)
        setupFirebase()
        setupFacebookLogin()
        setupGoogleLogin()
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        getPermission()
        checkSessions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackFacebook(requestCode, resultCode, data)
        callbackGoogle(requestCode, data)
    }

    private fun setupUI() {
        googleBtn.setOnClickListener {
            googleSignIn()
        }

        fbBtn.setOnClickListener{
            facebookBtn.performClick()
        }
    }

    private fun setupFirebase(){
        auth = FirebaseAuth.getInstance()
    }

    private fun setupFacebookLogin(){
        callbackManager = CallbackManager.Factory.create()

        facebookBtn.setPermissions("email", "public_profile")
        facebookBtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                facebookAuth(loginResult.accessToken)
            }

            override fun onCancel() {
                Snackbar.make(window.decorView.rootView, "Login dibatalkan!", Snackbar.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(applicationContext, "Err : $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callbackFacebook(requestCode: Int, resultCode: Int, data: Intent?){
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun facebookAuth(token: AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Snackbar.make(window.decorView.rootView, "Proses Login ...\nHarap Tunggu!", Snackbar.LENGTH_LONG).show()
                    if (task.isSuccessful) {
                        checkSessions()
                    } else {
                        Snackbar.make(window.decorView.rootView, "Proses Login Gagal!", Snackbar.LENGTH_LONG).show()
                    }
                }
    }

    private fun checkSessions(){
        if (auth.currentUser != null) {
            startActivity<HomeActivity>()
            finishAffinity()
        }
    }

    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
    }


    private fun googleAuth(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity<HomeActivity>()
            } else {
                Toast.makeText(this, "Metode Login Google Gagal :'v", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun callbackGoogle(requestCode: Int, data: Intent?){
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleAuth(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Metode Login Google Gagal :'v", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun googleSignIn() {
        val loginIntent: Intent = signInClient.signInIntent
        Snackbar.make(window.decorView.rootView, "Proses Login ...\nHarap Tunggu!", Snackbar.LENGTH_LONG).show()
        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    //Force Permission
    private fun getPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }
}