package com.huntersascension.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class for managing shared preferences
 */
class PreferenceManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "hunters_ascension_prefs"
        private const val KEY_LOGGED_IN_USER = "logged_in_user"
        private const val KEY_LAST_LOGIN_DATE = "last_login_date"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * Save the logged in user
     */
    fun setLoggedInUser(username: String) {
        prefs.edit()
            .putString(KEY_LOGGED_IN_USER, username)
            .putLong(KEY_LAST_LOGIN_DATE, System.currentTimeMillis())
            .apply()
    }
    
    /**
     * Get the logged in user
     */
    fun getLoggedInUser(): String? {
        return prefs.getString(KEY_LOGGED_IN_USER, null)
    }
    
    /**
     * Clear the logged in user (logout)
     */
    fun clearLoggedInUser() {
        prefs.edit()
            .remove(KEY_LOGGED_IN_USER)
            .apply()
    }
    
    /**
     * Get the last login date
     */
    fun getLastLoginDate(): Long {
        return prefs.getLong(KEY_LAST_LOGIN_DATE, 0)
    }
    
    /**
     * Check if this is the first time the app is launched
     */
    fun isFirstLaunch(): Boolean {
        val key = "first_launch"
        val isFirst = prefs.getBoolean(key, true)
        
        if (isFirst) {
            prefs.edit().putBoolean(key, false).apply()
        }
        
        return isFirst
    }
    
    /**
     * Save a boolean preference
     */
    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    
    /**
     * Get a boolean preference
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
    
    /**
     * Save an integer preference
     */
    fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
    
    /**
     * Get an integer preference
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }
    
    /**
     * Save a string preference
     */
    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    /**
     * Get a string preference
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }
}
