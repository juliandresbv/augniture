package com.augniture.beta.ui.augmented

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AugmentedViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cart Fragment"
    }
    val text: LiveData<String> = _text
}