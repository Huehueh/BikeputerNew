package com.example.bikeputernew.datastore.database

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ResearchDataRepository @Inject constructor(
    private val researchDataDao: ResearchDataDao
    ){
        suspend fun addResearchData(researchData: ResearchData) = researchDataDao.addData(researchData)
    }