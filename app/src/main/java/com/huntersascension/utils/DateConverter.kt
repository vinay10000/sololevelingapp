package com.huntersascension.utils

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converter for Date objects to be stored in Room database
 */
class DateConverter {
    /**
     * Convert timestamp to Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    /**
     * Convert Date to timestamp
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
