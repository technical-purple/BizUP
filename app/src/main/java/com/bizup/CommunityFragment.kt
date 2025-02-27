package com.bizup

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bizup.databinding.FragmentCommunityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var isExpanded = false
    private val expandedWidth = 200
    private val collapsedWidth = 64
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val view = binding.root

        setupServerIcon()
        setupChannelClicks()

        if (savedInstanceState == null) {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        }

        return view
    }

    private fun setupServerIcon() {
        binding.serverIcon.setOnClickListener {
            toggleNavigationViewWidth()
        }
    }

    private fun setupChannelClicks() {
        binding.channel1.setOnClickListener {
            showJoinChannelDialog("channel1")
        }

        binding.channel2.setOnClickListener {
            showJoinChannelDialog("channel2")
        }

        binding.channel3.setOnClickListener {
            showJoinChannelDialog("channel3")
        }
    }

    private fun showJoinChannelDialog(channelId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Join Channel")
            .setMessage("Do you want to join this channel?")
            .setPositiveButton("Yes") { _, _ ->
                joinChannel(channelId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun joinChannel(channelId: String) {
        currentUser?.let { user ->
            val userId = user.uid
            val userChannelData = hashMapOf(
                "userId" to userId,
                "channelId" to channelId,
            )

            firestore.collection("user_channels")
                .document("${userId}_$channelId")
                .set(userChannelData)
                .addOnSuccessListener {
                    parentFragmentManager.beginTransaction().replace(R.id.fragment_container, getChannelFragment(channelId)).commit()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun getChannelFragment(channelId: String): Fragment {
        return when (channelId) {
            "channel1" -> Channel1Fragment()
            "channel2" -> Channel2Fragment()
            "channel3" -> Channel3Fragment()
            else -> HomeFragment()
        }
    }

    private fun toggleNavigationViewWidth() {
        val targetWidth = if (isExpanded) {
            collapsedWidth
        } else {
            expandedWidth
        }

        val startWidth = if (isExpanded) {
            expandedWidth
        } else {
            collapsedWidth
        }

        val animator = ValueAnimator.ofInt(startWidth, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = binding.discordNavView.layoutParams
            layoutParams.width = value
            binding.discordNavView.layoutParams = layoutParams
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 300
        animator.start()

        isExpanded = !isExpanded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
