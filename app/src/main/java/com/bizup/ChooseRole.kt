package com.bizup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChooseRole : AppCompatActivity() {
    private var db: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val buttonLearner = findViewById<Button>(R.id.buttonLearner)
        val buttonInstructor = findViewById<Button>(R.id.buttonInstructor)

        buttonLearner.setOnClickListener { v: View? -> saveRoleToFirestore("learner") }
        buttonInstructor.setOnClickListener { v: View? -> saveRoleToFirestore("instructor") }
    }

    private fun saveRoleToFirestore(role: String) {
        val userId = firebaseAuth!!.currentUser!!.uid

        db!!.collection("account").document(userId)
            .update("role", role)
            .addOnSuccessListener { aVoid: Void? ->
                startActivity(Intent(this, Home::class.java))
                finish()
            }
            .addOnFailureListener { e: Exception? ->
                Toast.makeText(
                    this,
                    "Failed to save role.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}