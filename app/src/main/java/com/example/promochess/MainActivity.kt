package com.example.promochess


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.promochess.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Load HomeFragment if it's not already added
//        if (savedInstanceState == null) {
//            supportFragmentManager.commit {
//                replace(R.id.fragment_container, HomeFragment())
//            }
//        }

        // Set up the NavController with the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}
