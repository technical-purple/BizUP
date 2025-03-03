package com.bizup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bizup.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding!!.textSignup.setOnClickListener { v ->
            val intent =
                Intent(this, Signup::class.java)
            startActivity(intent)
            finish()
        }

        binding!!.textForgotpassword.setOnClickListener { v ->
            val intent = Intent(
                this,
                Forgotpassword::class.java
            )
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

            if (email.isEmpty()) {
                binding!!.emailField.error = "Email is required"
                return@setOnClickListener
            } else {
                binding!!.emailField.error = null
            }

            if (password.isEmpty()) {
                binding!!.passwordField.error = "Password is required"
                return@setOnClickListener
            } else {
                binding!!.passwordField.error = null
            }

            progressDialog = ProgressDialog(this)
            progressDialog!!.setMessage("Logging in...")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()

            firebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->

                    progressDialog!!.dismiss()

                    if (task.isSuccessful) {
                        val userId = firebaseAuth!!.currentUser!!.uid

                        firestore!!.collection("account").document(userId)
                            .get()
                            .addOnSuccessListener { document: DocumentSnapshot ->
                                if (document.exists()) {
                                    val role = document.getString("role")
                                    if ("learner" == role) {
                                        startActivity(
                                            Intent(
                                                this,
                                                Home::class.java
                                            )
                                        )
                                    } else if ("instructor" == role) {
                                        startActivity(
                                            Intent(
                                                this,
                                                HomeInstructor::class.java
                                            )
                                        )
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "User data not found.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidUserException) {
                            Toast.makeText(
                                this,
                                "No account found with this email.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(
                                this,
                                "Incorrect password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed. Please check your credentials and try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }
}