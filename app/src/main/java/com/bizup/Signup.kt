package com.bizup

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizup.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class Signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.emailField.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.passwordField.editText?.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.button.setOnClickListener {
            val email = binding.emailField.editText?.text.toString().trim()
            val password = binding.passwordField.editText?.text.toString().trim()
            val confirmPassword = binding.confirmPassword.editText?.text.toString().trim()

            // Clear previous errors
            binding.emailField.error = null
            binding.passwordField.error = null
            binding.confirmPassword.error = null

            if (email.isEmpty()) {
                binding.emailField.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordField.error = "Password is required"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                binding.confirmPassword.error = "Confirm password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthUserCollisionException -> {
                                binding.emailField.error = "An account already exists with this email."
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                binding.passwordField.error = "Invalid password format."
                            }
                            else -> {
                                Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }
}