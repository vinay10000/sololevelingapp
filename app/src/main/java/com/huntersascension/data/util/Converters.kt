package com.huntersascension.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huntersascension.data.model.*
import java.util.*

/**
 * TypeConverters for Room database
 */
class Converters {
    private val gson = Gson()

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Enum converters
    @TypeConverter
    fun fromRank(rank: Rank): String {
        return rank.name
    }

    @TypeConverter
    fun toRank(value: String): Rank {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromStat(stat: Stat): String {
        return stat.name
    }

    @TypeConverter
    fun toStat(value: String): Stat {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromWorkoutType(type: WorkoutType): String {
        return type.name
    }

    @TypeConverter
    fun toWorkoutType(value: String): WorkoutType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromWorkoutDifficulty(difficulty: WorkoutDifficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toWorkoutDifficulty(value: String): WorkoutDifficulty {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromExerciseCategory(category: ExerciseCategory): String {
        return category.name
    }

    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromExerciseType(type: ExerciseType): String {
        return type.name
    }

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromMuscleGroup(muscleGroup: MuscleGroup): String {
        return muscleGroup.name
    }

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromAchievementCategory(category: AchievementCategory): String {
        return category.name
    }

    @TypeConverter
    fun toAchievementCategory(value: String): AchievementCategory {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromAchievementTier(tier: AchievementTier): String {
        return tier.name
    }

    @TypeConverter
    fun toAchievementTier(value: String): AchievementTier {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromAbilityType(type: AbilityType): String {
        return type.name
    }

    @TypeConverter
    fun toAbilityType(value: String): AbilityType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromQuestType(type: QuestType): String {
        return type.name
    }

    @TypeConverter
    fun toQuestType(value: String): QuestType {
        return enumValueOf(value)
    }

    // List converters
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>): String {
        return gson.toJson(value.map { it.name })
    }

    @TypeConverter
    fun toMuscleGroupList(value: String): List<MuscleGroup> {
        val listType = object : TypeToken<List<String>>() {}.type
        val stringList: List<String> = gson.fromJson(value, listType)
        return stringList.map { enumValueOf<MuscleGroup>(it) }
    }
}
