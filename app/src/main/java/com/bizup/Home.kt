package com.bizup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bizup.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadFragment(HomeFragment())


        val bottomNav: BottomNavigationView = binding.navbar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeBtn -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.messageBtn -> {
                    loadFragment(MessageFragment())
                    true
                }
                R.id.communityBtn -> {
                    loadFragment(CommunityFragment())
                    true
                }
                R.id.profileBtn -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }
}