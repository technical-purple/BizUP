package com.bizup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class ChooseRole : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_role)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonLearner: Button = findViewById(R.id.buttonLearner)
        val buttonInstructor: Button = findViewById(R.id.buttonInstructor)

        buttonLearner.setOnClickListener {
            saveRoleToFirebase("learner")
            navigateToHome()
        }

        buttonInstructor.setOnClickListener {
            saveRoleToFirebase("instructor")
            navigateToHome()
        }
    }

    private fun saveRoleToFirebase(role: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users").child("user_id").child("role")

        myRef.setValue(role)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}