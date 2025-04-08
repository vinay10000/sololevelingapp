package com.huntersascension

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.huntersascension.ui.user.UserViewModel
import com.huntersascension.ui.util.ViewModelFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var userViewModel: UserViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Setup ViewModel
        val viewModelFactory = ViewModelFactory(application)
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]
        
        // Setup Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // Setup DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 
            R.string.navigation_drawer_open, 
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        // Setup NavigationView
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        
        // Setup NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Setup AppBarConfiguration for drawer integration
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_workouts, R.id.nav_profile, 
                R.id.nav_achievements, R.id.nav_quests, R.id.nav_abilities
            ), 
            drawerLayout
        )
        
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        
        // Update navigation drawer header with user info
        val headerView = navView.getHeaderView(0)
        updateNavigationHeader(headerView)
        
        // Observe login state to show/hide menu items
        userViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            updateMenuVisibility(navView.menu, isLoggedIn)
            if (isLoggedIn) {
                updateNavigationHeader(headerView)
            } else {
                // If not logged in and not on login/register screen, navigate to login
                if (navController.currentDestination?.id != R.id.nav_login && 
                    navController.currentDestination?.id != R.id.nav_register) {
                    navController.navigate(R.id.nav_login)
                }
            }
        }
    }
    
    /**
     * Updates the navigation drawer header with user info
     */
    private fun updateNavigationHeader(headerView: View) {
        val headerUsername = headerView.findViewById<TextView>(R.id.nav_header_username)
        val headerRank = headerView.findViewById<TextView>(R.id.nav_header_rank)
        val headerLevel = headerView.findViewById<TextView>(R.id.nav_header_level)
        val headerAvatar = headerView.findViewById<ImageView>(R.id.nav_header_avatar)
        
        // Update with user data if logged in
        userViewModel.currentUser.observe(this) { user ->
            if (user != null) {
                headerUsername.text = user.hunterName.ifEmpty { user.username }
                headerRank.text = "${user.rank}-Rank Hunter"
                headerLevel.text = "Level ${user.level}"
                
                // Set avatar image if available (using a library like Glide or Picasso)
                // if (user.avatarPath != null) {
                //     Glide.with(this).load(user.avatarPath).into(headerAvatar)
                // }
            }
        }
    }
    
    /**
     * Updates the visibility of menu items based on login state
     */
    private fun updateMenuVisibility(menu: Menu, isLoggedIn: Boolean) {
        // Show/hide menu items based on login state
        menu.findItem(R.id.nav_login)?.isVisible = !isLoggedIn
        menu.findItem(R.id.nav_register)?.isVisible = !isLoggedIn
        menu.findItem(R.id.nav_logout)?.isVisible = isLoggedIn
        
        // Main feature menu items only visible when logged in
        menu.findItem(R.id.nav_home)?.isVisible = isLoggedIn
        menu.findItem(R.id.nav_workouts)?.isVisible = isLoggedIn
        menu.findItem(R.id.nav_profile)?.isVisible = isLoggedIn
        menu.findItem(R.id.nav_achievements)?.isVisible = isLoggedIn
        menu.findItem(R.id.nav_quests)?.isVisible = isLoggedIn
        menu.findItem(R.id.nav_abilities)?.isVisible = isLoggedIn
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
            }
            R.id.nav_workouts -> {
                navController.navigate(R.id.nav_workouts)
            }
            R.id.nav_profile -> {
                navController.navigate(R.id.nav_profile)
            }
            R.id.nav_achievements -> {
                navController.navigate(R.id.nav_achievements)
            }
            R.id.nav_quests -> {
                navController.navigate(R.id.nav_quests)
            }
            R.id.nav_abilities -> {
                navController.navigate(R.id.nav_abilities)
            }
            R.id.nav_login -> {
                navController.navigate(R.id.nav_login)
            }
            R.id.nav_register -> {
                navController.navigate(R.id.nav_register)
            }
            R.id.nav_logout -> {
                userViewModel.logout()
                navController.navigate(R.id.nav_login)
            }
        }
        
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
