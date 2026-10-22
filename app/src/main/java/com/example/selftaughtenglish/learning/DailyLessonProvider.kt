package com.example.selftaughtenglish.learning

/**
 * 课程访问入口。
 *
 * 超出范围的天数会夹到有效区间，避免调用方传入 0 或负数时崩溃。
 */
class DailyLessonProvider {

    val totalDays: Int = LessonLibrary.lessons.size

    fun lessonForDay(day: Int): DailyLesson {
        val index = (day - 1).coerceIn(0, LessonLibrary.lessons.lastIndex)
        return LessonLibrary.lessons[index]
    }

    fun hasDay(day: Int): Boolean = day in 1..totalDays
}
