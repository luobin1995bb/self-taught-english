package com.example.selftaughtenglish.learning

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

/**
 * 练习进度的本地持久化。
 *
 * 用系统自带的 [SharedPreferences]，不引入 DataStore 依赖 —— 项目定位是离线、零第三方依赖。
 * 记录三类信息：哪些天已完成、连续练习天数、哪些天被标记为需要复习。
 */
class ProgressStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDayCompleted(day: Int): Boolean = prefs.getBoolean(dayKey(day), false)

    fun completedDays(): Set<Int> {
        val stored = prefs.getStringSet(KEY_COMPLETED_DAYS, emptySet()) ?: emptySet()
        return stored.mapNotNull { it.toIntOrNull() }.toSet()
    }

    /**
     * 标记某天完成，并更新连续天数。
     *
     * 连续规则：当天已记过不重复累加；昨天练过则 +1；断档则从 1 开始。
     */
    fun markDayCompleted(day: Int): Int {
        val days = completedDays().toMutableSet()
        days.add(day)
        prefs.edit()
            .putStringSet(KEY_COMPLETED_DAYS, days.map { it.toString() }.toSet())
            .apply()

        val today = todayIndex()
        val lastDay = prefs.getLong(KEY_LAST_COMPLETED_DAY, -1L)
        val previousStreak = prefs.getInt(KEY_STREAK, 0)
        val streak = when {
            lastDay == today -> previousStreak.coerceAtLeast(1)
            lastDay == today - 1 -> previousStreak + 1
            else -> 1
        }

        prefs.edit()
            .putLong(KEY_LAST_COMPLETED_DAY, today)
            .putInt(KEY_STREAK, streak)
            .apply()
        return streak
    }

    fun streak(): Int = prefs.getInt(KEY_STREAK, 0)

    fun reviewFlags(): Set<String> {
        val stored = prefs.getStringSet(KEY_REVIEW_FLAGS, emptySet()) ?: emptySet()
        return stored.toSet()
    }

    /** 切换某天的复习标记，返回切换后是否处于「需要复习」状态。 */
    fun toggleReview(day: Int): Boolean {
        val flags = reviewFlags().toMutableSet()
        val key = reviewKey(day)
        val marked = if (flags.contains(key)) {
            flags.remove(key)
            false
        } else {
            flags.add(key)
            true
        }
        prefs.edit().putStringSet(KEY_REVIEW_FLAGS, flags).apply()
        return marked
    }

    companion object {
        private const val PREFS_NAME = "self_taught_english_progress"
        private const val KEY_COMPLETED_DAYS = "completed_days"
        private const val KEY_REVIEW_FLAGS = "review_flags"
        private const val KEY_LAST_COMPLETED_DAY = "last_completed_day"
        private const val KEY_STREAK = "streak"

        private const val MILLIS_PER_DAY = 86_400_000L

        fun reviewKey(day: Int): String = "day-$day"

        private fun dayKey(day: Int): String = "day_completed_$day"

        /**
         * 本地时区下的「天序号」。
         *
         * 先把时间归零到本地午夜，再加上时区偏移，这样同一本地日期的任意时刻都落在同一个整数上。
         * 直接用毫秒数除以一天会在 UTC 午夜处切开本地日期，导致跨夜判断出错。
         */
        private fun todayIndex(): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val midnight = calendar.timeInMillis
            val offset = calendar.timeZone.getOffset(midnight)
            return (midnight + offset) / MILLIS_PER_DAY
        }
    }
}
