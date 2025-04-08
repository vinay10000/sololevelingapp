package com.huntersascension.ui.quest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestViewModel : ViewModel() {
    
    private val _text = MutableLiveData<String>().apply {
        value = "This is quest fragment"
    }
    val text: LiveData<String> = _text
}
