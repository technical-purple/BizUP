package com.bizup

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.bizup.databinding.FragmentCommunityBinding
import com.google.android.material.navigation.NavigationView

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationView: NavigationView
    private var isExpanded = false
    private val expandedWidth = 200
    private val collapsedWidth = 60

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val view = binding.root
        navigationView = binding.navView

        setupDrawer()

        if (savedInstanceState == null) {
            replaceFragment(Channel1Fragment())
        }

        return view
    }

    private fun setupDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_channel1 -> {
                    replaceFragment(Channel1Fragment())
                    toggleNavigationViewWidth()
                }

                R.id.nav_channel2 -> {
                    replaceFragment(Channel2Fragment())
                    toggleNavigationViewWidth()
                }

                R.id.nav_channel3 -> {
                    replaceFragment(Channel3Fragment())
                    toggleNavigationViewWidth()
                }

                else -> false
            }
            true
        }
        // Disable the "Community" menu item
        val menu = navigationView.menu
        val communityItem = menu.findItem(R.id.nav_open_drawer)
        communityItem.isEnabled = false
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
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
            val layoutParams = navigationView.layoutParams
            layoutParams.width = value.toDp()
            navigationView.layoutParams = layoutParams
        }
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 300
        animator.start()

        isExpanded = !isExpanded
    }

    private fun Int.toDp(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}