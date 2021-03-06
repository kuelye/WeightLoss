package com.levkorol.weightloss.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.levkorol.weightloss.R

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun signUpWithEmail(v: View) {
        val intent = Intent(this, SignUpWithEmailActivity::class.java)
        startActivity(intent)
    }

    fun signUpWithFacebook(v: View) {
        val intent = Intent(this, ProfileUserActivity::class.java)
        startActivity(intent)
    }
    
    fun signUpWithTwitter(v: View) {

    }
    fun back(view: View) {finish()}

    fun goToMenu(v: View) {
        val intent = Intent(this, ProfileActivity::class.java)
//        val songInfos: ArrayList<SongInfo> = arrayListOf()
//        intent.putExtra("as", songInfos)
        startActivity(intent)
    }
}
