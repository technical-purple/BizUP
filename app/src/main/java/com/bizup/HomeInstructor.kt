package com.bizup

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomeInstructor : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeinstructor)

        firestore = FirebaseFirestore.getInstance()

        val userId = intent.getStringExtra("userId") ?: return
        loadInstructorAnalytics(userId)
    }

    private fun loadInstructorAnalytics(userId: String) {
        firestore.collection("account").document(userId)
            .collection("role").document("instructor")
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e("HomeInstructor", "Error getting instructor data: ${error.message}")
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val learnersJoined = document.getLong("learners_joined") ?: 0
                    val meetingsConducted = document.getLong("meetings_conducted") ?: 0
                    val learnersInMeetings = document.getLong("learners_in_meetings") ?: 0

                    findViewById<TextView>(R.id.learner_count).text = "Learners Joined: $learnersJoined"
                    findViewById<TextView>(R.id.meetings_count).text = "Meetings Conducted: $meetingsConducted"
                    findViewById<TextView>(R.id.meeting_learners_count).text = "Learners in Meetings: $learnersInMeetings"
                } else {
                    Log.d("HomeInstructor", "Instructor document does not exist.")
                }
            }
    }
}