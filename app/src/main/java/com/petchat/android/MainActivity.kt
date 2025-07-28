package com.petchat.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.petchat.android.databinding.ActivityMainBinding
import com.petchat.android.fragments.ChatFragment
import com.petchat.android.fragments.LocationFragment
import com.petchat.android.fragments.PetFragment
import com.petchat.android.fragments.SocialFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        // 默认显示对话页面
        if (savedInstanceState == null) {
            loadFragment(ChatFragment(), CHAT_TAG)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_chat -> {
                    loadFragment(ChatFragment(), CHAT_TAG)
                    true
                }
                R.id.navigation_location -> {
                    loadFragment(LocationFragment(), LOCATION_TAG)
                    true
                }
                R.id.navigation_social -> {
                    loadFragment(SocialFragment(), SOCIAL_TAG)
                    true
                }
                R.id.navigation_pet -> {
                    loadFragment(PetFragment(), PET_TAG)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        // 检查是否已经存在该Fragment
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            
            // 隐藏当前Fragment
            currentFragment?.let { hide(it) }
            
            if (existingFragment != null) {
                // 如果Fragment已存在，显示它
                show(existingFragment)
                currentFragment = existingFragment
            } else {
                // 如果Fragment不存在，添加它
                add(R.id.fragment_container, fragment, tag)
                currentFragment = fragment
            }
        }
    }

    companion object {
        private const val CHAT_TAG = "ChatFragment"
        private const val LOCATION_TAG = "LocationFragment"
        private const val SOCIAL_TAG = "SocialFragment"
        private const val PET_TAG = "PetFragment"
    }
}