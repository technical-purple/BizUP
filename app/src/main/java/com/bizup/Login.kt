package com.bizup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import com.bizup.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.emailField.isHintEnabled = false
        binding.passwordField.isHintEnabled = false

        binding.textSignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
            finish()
        }

        binding.textForgotPassword.setOnClickListener {
            val email = binding.emailField.editText?.text.toString().trim()
            if (email.isEmpty()) {
                binding.emailField.error = "Email is required"
                return@setOnClickListener
            } else {
                binding.emailField.error = null
            }
            sendPasswordResetEmail(email)
        }

        binding.emailField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.passwordField.editText?.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.emailField.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.emailField.isHintEnabled = true
                binding.passwordField.isHintEnabled = false
            }
        }

        binding.passwordField.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordField.isHintEnabled = true
                binding.emailField.isHintEnabled = false
            }
        }

        binding.button.setOnClickListener {
            val email = binding.emailField.editText?.text.toString().trim()
            val password = binding.passwordField.editText?.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailField.error = "Email is required"
                return@setOnClickListener
            } else {
                binding.emailField.error = null
            }

            if (password.isEmpty()) {
                binding.passwordField.error = "Password is required"
                return@setOnClickListener
            } else {
                binding.passwordField.error = null
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(this, "No account found with this email.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Login failed. Please check your credentials and try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
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
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send password reset email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}