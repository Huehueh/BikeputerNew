package com.example.bikeputernew.datastore.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RDViewModel @Inject constructor(
    private val researchDataRepository: ResearchDataRepository
): ViewModel(){
    fun addNewResearchData(data: ByteArray) {
        val date = System.currentTimeMillis()
        val researchData = ResearchData(date, data)
        viewModelScope.launch {
            researchDataRepository.addResearchData(researchData)
        }
    }
}