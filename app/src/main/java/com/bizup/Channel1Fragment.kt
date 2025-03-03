package com.bizup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bizup.databinding.FragmentChannel1Binding

class Channel1Fragment : Fragment() {

    private var _binding: FragmentChannel1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannel1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationButton.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }
        binding.buttonAnnouncement.setOnClickListener {
            startActivity(Intent(context, AnnouncementActivity::class.java))
        }

        binding.buttonGeneral.setOnClickListener {
            startActivity(Intent(context, GeneralActivity::class.java))
        }

        binding.buttonDiscussions.setOnClickListener {
            startActivity(Intent(context, DiscussionsActivity::class.java))
        }

        binding.buttonMeetings.setOnClickListener {
            startActivity(Intent(context, MeetingsActivity::class.java))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}