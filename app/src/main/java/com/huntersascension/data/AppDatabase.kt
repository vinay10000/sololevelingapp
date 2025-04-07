package com.huntersascension.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.huntersascension.data.converter.Converters
import com.huntersascension.data.dao.UserDao
import com.huntersascension.data.dao.WorkoutDao
import com.huntersascension.data.dao.TrophyDao
import com.huntersascension.data.model.User
import com.huntersascension.data.model.Workout
import com.huntersascension.data.model.Trophy

@Database(
    entities = [User::class, Workout::class, Trophy::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun trophyDao(): TrophyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hunters_ascension_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
