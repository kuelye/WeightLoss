package com.levkorol.weightloss.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.levkorol.weightloss.R
import com.levkorol.weightloss.model.SongInfo
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializeNavigation()
    }

    fun goToPleer(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initializeNavigation() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profileItem -> {
                    val profileIntent = Intent(this, LoginPasswordActivity::class.java)
                    profileIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(profileIntent)
                }

                R.id.pleerItem -> {
                    val pleerIntent = Intent(this, MainActivity::class.java)
                    pleerIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(pleerIntent)
                }
                R.id.premiumItem -> {
                    val profileIntent = Intent(this, PremiumActivity::class.java)
                    profileIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(profileIntent)
                }
                R.id.inviteFriendsItem -> {
                    val profileIntent = Intent(this, InvateFriendsActivity::class.java)
                    profileIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(profileIntent)
                }
                R.id.instructionsItem -> {
                    val profileIntent = Intent(this, RegistrationActivity::class.java)
                    profileIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(profileIntent)
                }
                R.id.otherAppsItem-> {
                    val profileIntent = Intent(this, RegistrationActivity::class.java)
                    profileIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(profileIntent)
                }
                R.id.settingsItem -> {
                    val settingsIntent = Intent(this, SettingsActivity::class.java)
                    settingsIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(settingsIntent)
                }
                else -> {
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
}



