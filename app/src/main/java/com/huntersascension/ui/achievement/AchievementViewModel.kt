package com.huntersascension.ui.achievement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AchievementViewModel : ViewModel() {
    
    private val _text = MutableLiveData<String>().apply {
        value = "This is achievement fragment"
    }
    val text: LiveData<String> = _text
}
