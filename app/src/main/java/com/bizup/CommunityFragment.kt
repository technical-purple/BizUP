package com.bizup

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bizup.databinding.FragmentCommunityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommunityFragment : Fragment() {
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var isExpanded = false
    private val expandedWidth = 400
    private val collapsedWidth = 150
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
            checkUserJoinedAnyChannel()
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
            checkIfUserJoinedChannel("channel1")
        }
        binding.channel2.setOnClickListener {
            checkIfUserJoinedChannel("channel2")
        }
        binding.channel3.setOnClickListener {
            checkIfUserJoinedChannel("channel3")
        }
    }

    private fun checkUserJoinedAnyChannel() {
        currentUser?.let { user ->
            lifecycleScope.launch {
                try {
                    val querySnapshot = withContext(Dispatchers.IO) {
                        firestore.collection("user_channels")
                            .whereEqualTo("userId", user.uid)
                            .get()
                            .await()
                    }
                    if (!querySnapshot.isEmpty) {
                        val firstChannelId = querySnapshot.documents[0].getString("channelId")
                            ?: return@launch
                        // Hide the empty channel layout
                        binding.emptyChannelLayout.root.visibility = View.GONE
                        // Display the first channel
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, getChannelFragment(firstChannelId))
                            .commit()
                    } else {
                        // Show the empty channel layout
                        binding.emptyChannelLayout.root.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkIfUserJoinedChannel(channelId: String) {
        currentUser?.let { user ->
            val userId = user.uid
            val docId = "${userId}_$channelId"
            lifecycleScope.launch {
                try {
                    val document = withContext(Dispatchers.IO) {
                        firestore.collection("user_channels")
                            .document(docId)
                            .get()
                            .await()
                    }
                    if (document.exists()) {
                        // Hide the empty channel layout
                        binding.emptyChannelLayout.root.visibility = View.GONE
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, getChannelFragment(channelId))
                            .commit()
                    } else {
                        showJoinChannelDialog(channelId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showJoinChannelDialog(channelId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Join Channel")
            .setMessage("Do you want to join this channel?")
            .setPositiveButton("Yes") { _, _ -> joinChannel(channelId) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun joinChannel(channelId: String) {
        currentUser?.let { user ->
            val userId = user.uid
            val docId = "${userId}_$channelId"
            lifecycleScope.launch {
                try {
                    val document = withContext(Dispatchers.IO) {
                        firestore.collection("user_channels")
                            .document(docId)
                            .get()
                            .await()
                    }
                    if (document.exists()) {
                        // Hide the empty channel layout
                        binding.emptyChannelLayout.root.visibility = View.GONE
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, getChannelFragment(channelId))
                            .commit()
                    } else {
                        val userChannelData = hashMapOf(
                            "userId" to userId,
                            "channelId" to channelId
                        )
                        withContext(Dispatchers.IO) {
                            firestore.collection("user_channels")
                                .document(docId)
                                .set(userChannelData)
                                .await()
                        }
                        // Hide the empty channel layout
                        binding.emptyChannelLayout.root.visibility = View.GONE
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, getChannelFragment(channelId))
                            .commit()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
            binding.discordNavView.requestLayout()
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 200
        animator.start()
        isExpanded = !isExpanded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
