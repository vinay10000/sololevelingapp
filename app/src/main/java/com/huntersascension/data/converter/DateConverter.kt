package com.huntersascension.data.converter

import androidx.room.TypeConverter
import java.util.*

/**
 * Type converter for Room database to handle Date objects
 */
class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    
    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
