package com.example.bikeputernew.datastore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bikeputernew.values.Constants
import java.util.Base64

@Entity(tableName = Constants.RESEARCH_DB_TABLE)
class ResearchData (
    @PrimaryKey val time: Long,
    val data: ByteArray,
) {
    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(time)
        sb.append(",")
//        sb.append(Base64.getEncoder().encode(data))
        sb.append(data.toHexString())
        return sb.toString()
    }
}

