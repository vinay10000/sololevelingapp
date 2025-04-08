package com.huntersascension.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.huntersascension.data.dao.*
import com.huntersascension.data.dao.social.*
import com.huntersascension.data.model.*
import com.huntersascension.data.model.social.*
import com.huntersascension.data.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database class for the application
 */
@Database(
    entities = [
        User::class,
        Workout::class,
        Exercise::class,
        WorkoutExercise::class,
        WorkoutHistory::class,
        ExerciseHistory::class,
        Achievement::class,
        Ability::class,
        UserAbility::class,
        Quest::class,
        // Social and leaderboard entities
        FriendRelation::class,
        SocialShare::class,
        SocialLike::class,
        SocialComment::class,
        Leaderboard::class,
        LeaderboardEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutHistoryDao(): WorkoutHistoryDao
    abstract fun exerciseHistoryDao(): ExerciseHistoryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun abilityDao(): AbilityDao
    abstract fun questDao(): QuestDao
    
    // Social and leaderboard DAOs
    abstract fun friendDao(): FriendDao
    abstract fun socialDao(): SocialDao
    abstract fun leaderboardDao(): LeaderboardDao
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the database instance, creating it if necessary
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hunters_ascension_db"
                )
                .fallbackToDestructiveMigration() // For simplicity, in production use proper migrations
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate the database with initial data when first created
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(database)
                            }
                        }
                    }
                })
                .build()
                
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Populates the database with initial data
         */
        private suspend fun populateInitialData(database: AppDatabase) {
            // Add default exercises
            database.exerciseDao().insertExercises(getDefaultExercises())
            
            // Add default abilities
            database.abilityDao().insertAbilities(getAllAbilities())
            
            // Add default achievements
            database.achievementDao().insertAchievements(getDefaultAchievements())
            
            // Add sample workouts (optional)
            database.workoutDao().insertWorkouts(getSampleWorkouts())
            
            // Create default leaderboards
            createDefaultLeaderboards(database)
        }
        
        /**
         * Creates default leaderboards
         */
        private suspend fun createDefaultLeaderboards(database: AppDatabase) {
            val leaderboardDao = database.leaderboardDao()
            
            // Create global leaderboards
            leaderboardDao.createOrUpdateTotalExpLeaderboard()
            leaderboardDao.createOrUpdateWeeklyExpLeaderboard()
            leaderboardDao.createOrUpdateStreakLeaderboard()
            
            // Create stat leaderboards
            leaderboardDao.createOrUpdateStatLeaderboard(LeaderboardType.STRENGTH)
            leaderboardDao.createOrUpdateStatLeaderboard(LeaderboardType.ENDURANCE)
            leaderboardDao.createOrUpdateStatLeaderboard(LeaderboardType.AGILITY)
            leaderboardDao.createOrUpdateStatLeaderboard(LeaderboardType.VITALITY)
            
            // Create rank-specific leaderboards
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.E)
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.D)
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.C)
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.B)
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.A)
            leaderboardDao.createOrUpdateRankSpecificLeaderboard(Rank.S)
        }
        
        /**
         * Creates a list of default exercises
         */
        private fun getDefaultExercises(): List<Exercise> {
            // This would create a comprehensive list of default exercises
            // Here's a simplified version with just a few exercises
            
            return listOf(
                Exercise(
                    name = "Push-ups",
                    description = "Basic upper body exercise",
                    instructions = "Start in a plank position with hands shoulder-width apart. Lower your body until your chest nearly touches the floor, then push back up.",
                    category = ExerciseCategory.STRENGTH,
                    type = ExerciseType.REPETITION_BASED,
                    primaryMuscle = MuscleGroup.CHEST,
                    secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
                    primaryStat = Stat.STRENGTH,
                    secondaryStat = Stat.ENDURANCE,
                    difficulty = 2,
                    defaultSets = 3,
                    defaultReps = 12,
                    defaultRestTime = 60
                ),
                Exercise(
                    name = "Squats",
                    description = "Fundamental lower body exercise",
                    instructions = "Stand with feet shoulder-width apart. Lower your body as if sitting in a chair, keeping knees over toes. Return to standing position.",
                    category = ExerciseCategory.STRENGTH,
                    type = ExerciseType.REPETITION_BASED,
                    primaryMuscle = MuscleGroup.QUADRICEPS,
                    secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
                    primaryStat = Stat.STRENGTH,
                    secondaryStat = Stat.VITALITY,
                    difficulty = 2,
                    defaultSets = 3,
                    defaultReps = 15,
                    defaultRestTime = 60
                ),
                Exercise(
                    name = "Jogging",
                    description = "Basic cardio exercise",
                    instructions = "Run at a moderate pace, maintaining consistent breathing and form.",
                    category = ExerciseCategory.CARDIO,
                    type = ExerciseType.DISTANCE_BASED,
                    primaryMuscle = MuscleGroup.LOWER_BODY,
                    secondaryMuscles = listOf(MuscleGroup.CORE),
                    primaryStat = Stat.ENDURANCE,
                    secondaryStat = Stat.VITALITY,
                    difficulty = 2,
                    defaultDuration = 1200, // 20 minutes
                    defaultDistance = 3000f, // 3 km
                    defaultRestTime = 0
                ),
                Exercise(
                    name = "Plank",
                    description = "Core stability exercise",
                    instructions = "Hold a push-up position with your weight on your forearms. Keep your body in a straight line from head to heels.",
                    category = ExerciseCategory.STRENGTH,
                    type = ExerciseType.TIME_BASED,
                    primaryMuscle = MuscleGroup.ABS,
                    secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.LOWER_BACK),
                    primaryStat = Stat.VITALITY,
                    secondaryStat = Stat.STRENGTH,
                    difficulty = 2,
                    defaultSets = 3,
                    defaultDuration = 60, // 60 seconds
                    defaultRestTime = 45
                ),
                Exercise(
                    name = "Jumping Jacks",
                    description = "Full-body cardio exercise",
                    instructions = "Stand with feet together and arms at sides. Jump to a position with legs spread and arms overhead. Return to starting position.",
                    category = ExerciseCategory.CARDIO,
                    type = ExerciseType.TIME_BASED,
                    primaryMuscle = MuscleGroup.FULL_BODY,
                    primaryStat = Stat.AGILITY,
                    secondaryStat = Stat.ENDURANCE,
                    difficulty = 1,
                    defaultSets = 3,
                    defaultDuration = 45, // 45 seconds
                    defaultRestTime = 30
                )
                // Additional exercises would be added here
            )
        }
        
        /**
         * Creates a list of all abilities from all ranks
         */
        private fun getAllAbilities(): List<Ability> {
            val allAbilities = mutableListOf<Ability>()
            
            // Collect abilities from all ranks
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.E))
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.D))
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.C))
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.B))
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.A))
            allAbilities.addAll(AbilityData.getNewAbilitiesForRank(Rank.S))
            
            return allAbilities
        }
        
        /**
         * Creates a list of default achievements
         */
        private fun getDefaultAchievements(): List<Achievement> {
            // This would create a comprehensive list of achievements
            // Here's a simplified version with just a few
            
            return listOf(
                Achievement(
                    title = "First Workout",
                    description = "Complete your first workout",
                    category = AchievementCategory.WORKOUT_COUNT,
                    tier = AchievementTier.BRONZE,
                    requirement = "Complete 1 workout",
                    targetValue = 1,
                    expReward = 50
                ),
                Achievement(
                    title = "Workout Warrior",
                    description = "Complete 10 workouts",
                    category = AchievementCategory.WORKOUT_COUNT,
                    tier = AchievementTier.SILVER,
                    requirement = "Complete 10 workouts",
                    targetValue = 10,
                    expReward = 100,
                    strengthReward = 1
                ),
                Achievement(
                    title = "Exercise Master",
                    description = "Complete 100 workouts",
                    category = AchievementCategory.WORKOUT_COUNT,
                    tier = AchievementTier.GOLD,
                    requirement = "Complete 100 workouts",
                    targetValue = 100,
                    expReward = 500,
                    strengthReward = 3,
                    enduranceReward = 2
                ),
                Achievement(
                    title = "Week Streak",
                    description = "Maintain a 7-day workout streak",
                    category = AchievementCategory.STREAK,
                    tier = AchievementTier.BRONZE,
                    requirement = "Workout for 7 consecutive days",
                    targetValue = 7,
                    expReward = 100,
                    strengthReward = 1,
                    trophyPoints = 1
                ),
                Achievement(
                    title = "Month Streak",
                    description = "Maintain a 30-day workout streak",
                    category = AchievementCategory.STREAK,
                    tier = AchievementTier.GOLD,
                    requirement = "Workout for 30 consecutive days",
                    targetValue = 30,
                    expReward = 500,
                    vitalityReward = 3,
                    trophyPoints = 5
                ),
                Achievement(
                    title = "Calorie Crusher",
                    description = "Burn 1,000 total calories",
                    category = AchievementCategory.CALORIES,
                    tier = AchievementTier.BRONZE,
                    requirement = "Burn 1,000 calories through workouts",
                    targetValue = 1000,
                    expReward = 200,
                    enduranceReward = 1
                )
                // Additional achievements would be added here
            )
        }
        
        /**
         * Creates a list of sample workouts
         */
        private fun getSampleWorkouts(): List<Workout> {
            // This would create sample workouts
            // Here's a simplified version with just a few
            
            return listOf(
                Workout(
                    name = "Full Body Beginner",
                    description = "A simple full-body workout for beginners",
                    createdBy = "system",
                    type = WorkoutType.STRENGTH,
                    difficulty = WorkoutDifficulty.EASY,
                    primaryStat = Stat.STRENGTH,
                    secondaryStat = Stat.VITALITY,
                    isPublic = true,
                    isRecommended = true,
                    estimatedDuration = 20,
                    baseExpReward = 40
                ),
                Workout(
                    name = "Cardio Blast",
                    description = "High-intensity cardio workout",
                    createdBy = "system",
                    type = WorkoutType.CARDIO,
                    difficulty = WorkoutDifficulty.MEDIUM,
                    primaryStat = Stat.ENDURANCE,
                    secondaryStat = Stat.AGILITY,
                    isPublic = true,
                    isRecommended = true,
                    estimatedDuration = 30,
                    baseExpReward = 60
                ),
                Workout(
                    name = "Strength Builder",
                    description = "Focus on building raw strength",
                    createdBy = "system",
                    type = WorkoutType.STRENGTH,
                    difficulty = WorkoutDifficulty.HARD,
                    primaryStat = Stat.STRENGTH,
                    secondaryStat = null,
                    isPublic = true,
                    requiredRank = Rank.D,
                    estimatedDuration = 45,
                    baseExpReward = 80
                )
                // Additional workouts would be added here
            )
        }
    }
}
