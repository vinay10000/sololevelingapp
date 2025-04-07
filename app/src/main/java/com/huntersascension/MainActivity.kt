package com.huntersascension

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        
        // Setup the bottom navigation with the nav controller
        navView.setupWithNavController(navController)
        
        // Configure bottom navigation visibility based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.rankUpFragment -> {
                    navView.visibility = android.view.View.GONE
                }
                else -> {
                    navView.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
}
