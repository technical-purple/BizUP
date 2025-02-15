package com.bizup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizup.databinding.ActivityForgotpasswordBinding
import com.google.firebase.auth.FirebaseAuth

class Forgotpassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textBacktologin.setOnClickListener {
            navigateToLogin()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailField.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailField.error = "Email is required"
                return@setOnClickListener
            } else {
                binding.emailField.error = null
            }

            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent to $email",
                        Toast.LENGTH_LONG
                    ).show()
                    navigateToLogin()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send password reset email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}