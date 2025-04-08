package com.huntersascension.ui.ability

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AbilityViewModel : ViewModel() {
    
    private val _text = MutableLiveData<String>().apply {
        value = "This is ability fragment"
    }
    val text: LiveData<String> = _text
}
