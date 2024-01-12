package com.example.bikeputernew.datastore.database

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bikeputernew.values.Constants
import java.io.File

@Database(entities = [Trip::class, ResearchData::class], version = 1, exportSchema = false)
abstract class BikeDatabase: RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun researchDataDao(): ResearchDataDao

    companion object {
        private var instance: BikeDatabase? = null
        fun getInstance(context: Context): BikeDatabase {
            if (instance==null) {
                instance = Room.databaseBuilder(context,BikeDatabase::class.java,
                    Constants.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build()
            }
            return instance as BikeDatabase
        }
    }

    fun rdToCsvFile(){
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val csvFile = File(path, "huehue.csv")
        val sb = StringBuilder()
        var afterFirst = false
        for (rd in researchDataDao().getAllResearchData()) {
            if(afterFirst) sb.append("\n")
            afterFirst = true
            sb.append(rd.toString())
        }
        val text = sb.toString()
        csvFile.appendText(text)
        Log.i("DB", "Data saved to " + csvFile.absolutePath)
    }
}