package com.bizup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizup.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class Signup : AppCompatActivity() {
    private var binding: ActivitySignupBinding? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Signing up...")
        progressDialog!!.setCancelable(false)

        binding!!.textLogin.setOnClickListener { v ->
            val intent =
                Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding!!.emailField.editText!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding!!.passwordField.editText!!.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding!!.button.setOnClickListener { v ->
            val email =
                binding!!.emailField.editText!!.text.toString().trim()
            val password =
                binding!!.passwordField.editText!!.text.toString().trim()
            val confirmPassword =
                binding!!.confirmPassword.editText!!.text.toString().trim()

            binding!!.emailField.error = null
            binding!!.passwordField.error = null
            binding!!.confirmPassword.error = null

            if (email.isEmpty()) {
                binding!!.emailField.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding!!.passwordField.error = "Password is required"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                binding!!.confirmPassword.error = "Confirm password is required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding!!.confirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            progressDialog!!.show()

            firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    progressDialog!!.dismiss()
                    if (task.isSuccessful) {
                        val userId = firebaseAuth!!.currentUser!!.uid
                        saveUserToFirestore(userId, email)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            binding!!.emailField.error =
                                "An account already exists with this email."
                        } else if (exception is FirebaseAuthInvalidCredentialsException) {
                            binding!!.passwordField.error = "Invalid password format."
                        } else {
                            Toast.makeText(
                                this,
                                "Sign up failed. Make sure you have internet. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    private fun saveUserToFirestore(userId: String, email: String) {
        val user = HashMap<String, Any>()
        user["email"] = email
        user["role"] = ""

        db!!.collection("account").document(userId).set(user)
            .addOnSuccessListener { aVoid: Void? ->
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ChooseRole::class.java))
                finish()
            }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    this,
                    "Failed to save user data.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
