package com.huntersascension.data.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * TypeConverter for Room to convert Date objects to and from Long for database storage
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
