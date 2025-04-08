package com.huntersascension.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.huntersascension.data.converter.DateConverter
import com.huntersascension.data.dao.*
import com.huntersascension.data.model.*

/**
 * The Room database for the app
 */
@Database(
    entities = [
        User::class,
        Exercise::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutHistory::class,
        ExerciseHistory::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Get the UserDao
     * @return The UserDao
     */
    abstract fun userDao(): UserDao
    
    /**
     * Get the ExerciseDao
     * @return The ExerciseDao
     */
    abstract fun exerciseDao(): ExerciseDao
    
    /**
     * Get the WorkoutDao
     * @return The WorkoutDao
     */
    abstract fun workoutDao(): WorkoutDao
    
    /**
     * Get the WorkoutExerciseDao
     * @return The WorkoutExerciseDao
     */
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    
    /**
     * Get the WorkoutHistoryDao
     * @return The WorkoutHistoryDao
     */
    abstract fun workoutHistoryDao(): WorkoutHistoryDao
    
    /**
     * Get the ExerciseHistoryDao
     * @return The ExerciseHistoryDao
     */
    abstract fun exerciseHistoryDao(): ExerciseHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get the singleton instance of the database
         * @param context The context
         * @return The database instance
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hunters_ascension_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
