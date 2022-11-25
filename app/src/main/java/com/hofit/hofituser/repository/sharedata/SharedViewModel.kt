package com.hofit.hofituser.repository.sharedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel  : ViewModel() {

    // Mutable LiveData which observed by LiveData
    // and updated to EditTexts when it is changed.
    private val mutableLiveData = MutableLiveData<String>()

    // function to set the changed
    // data from the EditTexts
    fun setData(input: String) {
        mutableLiveData.value = input
    }

    // function to get the changed data from the EditTexts
    fun getData(): MutableLiveData<String> = mutableLiveData
}