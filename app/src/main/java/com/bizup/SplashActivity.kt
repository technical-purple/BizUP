package com.bizup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()


        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {

            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else {

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        finish()
    }
}